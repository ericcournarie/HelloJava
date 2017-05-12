/*
 * Copyright NellArmonia 2014
 */
package org.cournarie.patterns.observer;

import java.lang.reflect.Method;
import org.cournarie.patterns.observer.EventCallback.MethodException;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class ListenerTest {

    /** Call counts. */
    private int callCount = 0;
    /** Kill counts. */
    private int killCount = 0;

    @Test
    public void testListen() {
        EventListeners listeners = new EventListeners();

        MyEventListener listener = new MyEventListener();
        BrokenListener bListener = new BrokenListener();
        listeners.addListener(MyEvent.class, bListener, BrokenListener.KILL);
        listeners.addListener(MyEvent.class, listener, MyEventListener.CALL_ME);

        // this one should work
        MyEvent myEvent = new MyEvent(this);
        try {
            listeners.fireEvent(myEvent);
            Assert.fail("Exception should have be raised...");
        } catch (MethodException exc) {
            // this one is expected
        }
        Assert.assertEquals(1, killCount);

        // lets remove the killer one
        listeners.removeListener(MyEvent.class, bListener, BrokenListener.KILL);
        listeners.fireEvent(myEvent);
        Assert.assertEquals(1, callCount);

        // this one should not do anything
        AnotherEvent anotherEvent = new AnotherEvent(this);
        listeners.fireEvent(anotherEvent);
        Assert.assertEquals(1, callCount);

        // removes listener and checks it does nothing
        listeners.removeListener(MyEvent.class, listener, MyEventListener.CALL_ME);
        listeners.fireEvent(myEvent);

        Assert.assertEquals(1, callCount);
    }

    void called() {
        callCount += 1;
    }

    void killed() {
        killCount += 1;
    }

    /**
     * Event definition.
     */
    public static class MyEvent extends EventObject<ListenerTest> {

        MyEvent(ListenerTest src) {
            super(src);
        }
    }

    /**
     * Yet another event definition.
     */
    public static class AnotherEvent extends EventObject<ListenerTest> {

        AnotherEvent(ListenerTest src) {
            super(src);
        }
    }

    /**
     * Simple event listener.
     */
    public static class MyEventListener {

        static final Method CALL_ME;

        static {
            try {
                CALL_ME = MyEventListener.class.getDeclaredMethod("callMe", new Class[]{MyEvent.class});
            } catch (final java.lang.NoSuchMethodException e) {
                throw new java.lang.RuntimeException("Internal error", e);
            }
        }

        public void callMe(MyEvent evt) {
            evt.getSource().called();
        }
    }

    /**
     * A broken one that tries to kill..
     */
    public static class BrokenListener {

        static final Method KILL;

        static {
            try {
                KILL = BrokenListener.class.getDeclaredMethod("kill", new Class[]{MyEvent.class});
            } catch (final java.lang.NoSuchMethodException e) {
                throw new java.lang.RuntimeException("Internal error", e);
            }
        }

        public void kill(MyEvent evt) {
            evt.getSource().killed();
            throw new RuntimeException("Can I broke others ??");
        }
    }
}
