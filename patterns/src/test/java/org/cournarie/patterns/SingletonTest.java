/*
 * Copyright Eric Cournarie 2017
 */
package org.cournarie.patterns;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import static org.awaitility.Awaitility.await;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class SingletonTest {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(SingletonTest.class);
    /** Number of thread to run. */
    private static final int POOL_SIZE = 20;
    /** Thread pool. */
    private final ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);

    @Test
    public void testMultiThread() {
        List<Callable<Singleton>> workers = new ArrayList<>(POOL_SIZE);
        for (int i = 0; i < POOL_SIZE; i++) {
            workers.add(() -> run());
        }
        LOGGER.info("submitting " + workers.size() + " workers..");
        List<Future<Singleton>> tasks = new ArrayList<>(POOL_SIZE);
        for (Callable<Singleton> w : workers) {
            Future<Singleton> task = pool.submit(w);
            tasks.add(task);
        }
        LOGGER.info("waiting end");
        // if it takes more than 30 seconds, check your hardware ...
        await().atMost(30, TimeUnit.SECONDS).untilTrue(allDone(tasks));

        long resultCount = tasks.stream().map(f -> get(f)).count();
        Assert.assertEquals(POOL_SIZE, resultCount);
        // be sure all have the same object....
        int singletonCount = tasks.stream().map(f -> get(f)).
                collect(Collectors.toMap(t -> t, t -> t, (u, v) -> merge(u, v), IdentityHashMap::new)).size();
        Assert.assertEquals(1, singletonCount);
    }

    Singleton merge(Singleton s1, Singleton s2) {
        if (s1 == s2) {
            return s1;
        }
        throw new IllegalStateException("Several singleton found, something strange happened...");
    }

    private Singleton get(Future<Singleton> f) {
        try {
            return f.get();
        } catch (InterruptedException | ExecutionException ex) {
            Assert.fail(ex.getMessage());
            return null;
        }
    }

    private AtomicBoolean allDone(List<Future<Singleton>> tasks) {
        for (Future<Singleton> f : tasks) {
            if (!f.isDone()) {
                return new AtomicBoolean(false);
            }
        }
        return new AtomicBoolean(true);
    }

    private Singleton run() throws Exception {
        Singleton s = Singleton.getInstance();
        return s;
    }

}
