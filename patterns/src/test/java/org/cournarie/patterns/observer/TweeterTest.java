/*
 * Copyright NellArmonia 2014
 */
package org.cournarie.patterns.observer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.log4j.Logger;
import static org.awaitility.Awaitility.await;
import org.junit.Assert;
import org.junit.Test;

/**
 * A more concrete test.
 */
public class TweeterTest {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(TweeterTest.class);
    /** How many people in the world. */
    private static final int PEOPLE_COUNT = 4;
    /** How many tweet everyone can send. */
    private static final int TWEET_COUNT = 10;
    /** Thread pool. */
    private static final ExecutorService POOL = Executors.newFixedThreadPool(PEOPLE_COUNT);
    /** Only one exist, may have use a singleton here... */
    private static final Tweeter TWITTER = new Tweeter();
    /** Everyone tweet at the same time... */
    private static final List<Callable<Tweetee>> WORLD = new ArrayList<>(PEOPLE_COUNT);

    @Test
    public void testTweets() {

        Tweeter t = new Tweeter();
        Tweetee john = createTweetee("john");
        Tweetee emma = createTweetee("emma");
        Tweetee bob = createTweetee("bob");
        Tweetee beth = createTweetee("beth");

        // only girls follow the likes...
        TWITTER.addLiker(beth);
        TWITTER.addLiker(emma);

        LOGGER.info("Starting world for tweeters..");
        List<Future<Tweetee>> tasks = new ArrayList<>(PEOPLE_COUNT);
        WORLD.forEach(w -> tasks.add(POOL.submit(w)));

        LOGGER.info("waiting end of world...");
        // if it takes more than 30 seconds, check your hardware ...
        await().pollInterval(1, TimeUnit.SECONDS).pollDelay(1, TimeUnit.SECONDS)
                .atMost(30, TimeUnit.SECONDS).untilTrue(allDone(tasks));

        LOGGER.info("This is now a slient world.");

        Assert.assertEquals(TWEET_COUNT * PEOPLE_COUNT, TWITTER.getTweetCount());
        // everyone like other tweets...
        Assert.assertEquals(TWEET_COUNT * PEOPLE_COUNT * (PEOPLE_COUNT - 1), TWITTER.getLikeCount());

        /** everyone should have received all tweet sent by the others. */
        Assert.assertEquals(TWEET_COUNT * (PEOPLE_COUNT - 1), john.getTweets());
        Assert.assertEquals(TWEET_COUNT * (PEOPLE_COUNT - 1), emma.getTweets());
        Assert.assertEquals(TWEET_COUNT * (PEOPLE_COUNT - 1), bob.getTweets());
        Assert.assertEquals(TWEET_COUNT * (PEOPLE_COUNT - 1), beth.getTweets());
        /** only girls should have received likes, all likes but mines are not accounted. */
        Assert.assertEquals(0, john.getLikes());
        Assert.assertEquals(TWEET_COUNT * (PEOPLE_COUNT - 1) * (PEOPLE_COUNT - 1), emma.getLikes());
        Assert.assertEquals(0, bob.getLikes());
        Assert.assertEquals(TWEET_COUNT * (PEOPLE_COUNT - 1) * (PEOPLE_COUNT - 1), beth.getLikes());

    }

    Tweetee createTweetee(String name) {
        Tweetee t = new Tweetee(name);
        WORLD.add(() -> run(t));
        TWITTER.addAccount(t);
        return t;
    }

    /**
     * The tweeter manager, pronounce Twitter...
     */
    static class Tweeter {

        /** Listeners. */
        private final EventListeners listeners = new EventListeners();
        /** World statistics. */
        private int tweetSend = 0;
        /** Love statistics. */
        private int likeSend = 0;

        public int getTweetCount() {
            return tweetSend;
        }

        public int getLikeCount() {
            return likeSend;
        }

        /**
         * So I can receive my likes...
         * */
        public void addLiker(Tweetee t) {
            listeners.addListener(Like.class, t, Tweetee.LIKE_ME);
        }

        /**
         * I want to follow this buddy tweets...
         * @param t
         */
        public void addAccount(Tweetee t) {
            listeners.addListener(Tweet.class, t, Tweetee.TWEET_ME);
        }

        /**
         * Send a tweet.
         * @param account
         * @param tweet
         */
        public void tweet(Tweetee account, String tweet) {
            Tweet t = new Tweet(account, tweet);
            listeners.fireEvent(t);
            tweetSend += 1;
        }

        /**
         * Send a like.
         * @param account
         * @param tweet
         */
        public void like(Tweetee account, Tweet tweet) {
            Like l = new Like(tweet.getSource(), tweet);
            listeners.fireEvent(l);
            likeSend += 1;
        }
    }

    /**
     * Someone using 'Twitter'.
     */
    static class Tweetee {

        /** the method to call on tweet receive. */
        static final Method TWEET_ME;
        /** the method to call on tweet like. */
        static final Method LIKE_ME;
        /** Account name. */
        private final String name;
        /** Tweet count. */
        private int tweets = 0;
        /** Like count. */
        private int likes = 0;

        /** Method initialize. */
        static {
            try {
                TWEET_ME = Tweetee.class.getDeclaredMethod("tweet", new Class[]{Tweet.class});
            } catch (final java.lang.NoSuchMethodException e) {
                throw new java.lang.RuntimeException("Internal error, cannot tweet...", e);
            }
            try {
                LIKE_ME = Tweetee.class.getDeclaredMethod("like", new Class[]{Like.class});
            } catch (final java.lang.NoSuchMethodException e) {
                throw new java.lang.RuntimeException("Internal error, cannot tweet...", e);
            }
        }

        /**
         * Creates a new tweet account.
         * @param name
         */
        public Tweetee(String name) {
            this.name = name;
        }

        /**
         * Get the number of tweet received.
         * @return
         */
        public int getTweets() {
            return tweets;
        }

        /**
         * Get the number of likes received.
         * @return
         */
        public int getLikes() {
            return likes;
        }

        /**
         * Receive a tweet.
         * @param t
         */
        public void tweet(Tweet t) {
            // if it's not me...
            if (!this.name.equals(t.getSource().name)) {
                tweets += 1;
                // I like everything
                TWITTER.like(this, t);
            }
        }

        /**
         * Receive a like.
         * @param t
         */
        public void like(Like t) {
            // do not like my own tweets...
            if (!this.name.equals(t.getSource().name)) {
                likes += 1;
            }
        }

    }

    /**
     * The tweet.
     */
    static class Tweet extends EventObject<Tweetee> {

        /** Tweet. */
        private final String tweet;

        /**
         * A tweet post..
         * @param src
         * @param tweet
         */
        Tweet(Tweetee src, String tweet) {
            super(src);
            this.tweet = tweet;
        }
    }

    /**
     * The like.
     */
    static class Like extends EventObject<Tweetee> {

        /** Tweet. */
        private final Tweet tweet;

        /**
         * A tweet like...
         * @param src
         * @param tweet
         */
        Like(Tweetee src, Tweet tweet) {
            super(src);
            this.tweet = tweet;
        }
    }

    /**
     * Send all tweets.
     * @param t
     * @return
     * @throws Exception
     */
    private Tweetee run(Tweetee t) throws Exception {
        LOGGER.info(t.name + " start tweeting...");
        for (int i = 0; i < TWEET_COUNT; i++) {
            TWITTER.tweet(t, "@" + t.name + " , tweet numner " + i);
        }
        LOGGER.info(t.name + " stop tweeting...");
        return t;
    }

    /**
     * Are all people done with their tweets?
     * @param tasks
     * @return
     */
    private AtomicBoolean allDone(List<Future<Tweetee>> tasks) {
        for (Future<Tweetee> f : tasks) {
            if (!f.isDone()) {
//                try {
//                    LOGGER.info(f.get().name + " still not finished...");
//                } catch (InterruptedException | ExecutionException ex) {
//                }
                return new AtomicBoolean(false);
            }
        }
        return new AtomicBoolean(true);
    }
}
