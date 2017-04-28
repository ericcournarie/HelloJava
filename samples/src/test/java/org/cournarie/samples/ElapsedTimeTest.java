/*
 * Copyright Eric Cournarie 2017
 */
package org.cournarie.samples;

//static import org.awaitility.Awaitility.
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.log4j.Logger;
import static org.awaitility.Awaitility.await;
import org.junit.Assert;
import org.junit.Test;

/**
 * A JUNIT test .
 */
public class ElapsedTimeTest {

    /** Logger. */
    static private final Logger LOGGER = Logger.getLogger(ElapsedTimeTest.class);
    /** How much time do we sleep for the test?. */
    private static final int SLEEP = 100;

    @Test
    public void testTimer() {
        VerboseTimer timer = new VerboseTimer();
        timer.start();

        await().atLeast(SLEEP, TimeUnit.MILLISECONDS).untilTrue(new AtomicBoolean(true));

        timer.stop();

        double seconds = timer.elapse();
        Assert.assertTrue(seconds < 0.2F);
        long ms = timer.elapseTime();
        Assert.assertTrue(ms < (2 * SLEEP));
        Assert.assertTrue(ms > (SLEEP / 2));
        String s = timer.format();
        LOGGER.info("slept during " + s);
        Assert.assertTrue(s.indexOf("milliseconds") > 0);

        timer.reset();
        await().atLeast(SLEEP, TimeUnit.MILLISECONDS).untilTrue(new AtomicBoolean(true));
        timer.stop();
        ms = timer.elapseTime();
        LOGGER.info("slept during " + timer.format());
        Assert.assertTrue("Expected < " + (2 * SLEEP) + ", got " + ms, ms < (2 * SLEEP));
        Assert.assertTrue("Expected > " + (SLEEP / 2) + ", got " + ms, ms > (SLEEP / 2));
    }

    @Test
    public void testFormats() {
        String s = ElapsedTime.format(1000);
        Assert.assertEquals("1000 milliseconds", s);
        s = ElapsedTime.shortFormat(1000);
        Assert.assertEquals("1000ms", s);
        s = ElapsedTime.format(7800);
        Assert.assertEquals("7 seconds", s);
        s = ElapsedTime.shortFormat(7800);
        Assert.assertEquals("7s", s);
        s = ElapsedTime.format(97800);
        Assert.assertEquals("1 minute, 37 seconds", s);
        s = ElapsedTime.shortFormat(97800);
        Assert.assertEquals("1mn, 37s", s);
        s = ElapsedTime.format(13750000);
        Assert.assertEquals("3 hours, 49 minutes", s);
        s = ElapsedTime.shortFormat(13750000);
        Assert.assertEquals("3h, 49mn", s);
        s = ElapsedTime.clockFormat(13750000);
        Assert.assertEquals("03:49:10:000", s);
    }
}
