/*
 * Copyright Eric Cournarie 2017
 */
package org.cournarie.patterns.observer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * An event listener callback.
 */
class EventCallback {

    /** The event type expected by this callback. */
    private final Class<? extends EventObject> eventType;
    /** The target object to call. */
    private final Object target;
    /** The callback method. */
    private final Method callback;

    /**
     * Creates a new event callback.
     * @param eventType
     * @param object
     * @param method
     */
    public EventCallback(Class<? extends EventObject> eventType, Object object, Method method) {
        this.eventType = eventType;
        target = object;
        callback = method;
    }

    /**
     * Sends the event.
     * @param event
     * @throws MethodException
     */
    public void sendEvent(EventObject<?> event) {
        // Only send events supported by the method
        if (eventType.isAssignableFrom(event.getClass())) {
            try {
                callback.invoke(target, new Object[]{event});
            } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException ex) {
                throw new MethodException("sendEvent failure for '" + event + "'", ex);
            }
        }
    }

    /**
     * Checks a callback matches the arguments.
     *
     * @param eventType
     * @param target
     * @param method
     * @return
     */
    public boolean match(Class<?> eventType, Object target, Method method) {
        return (target == this.target)
                && eventType.equals(this.eventType)
                && method.equals(this.callback);
    }

    /**
     * The exception thrown is the event call produce an error.
     */
    public class MethodException extends RuntimeException {

        /**
         * Constructor.
         * @param message
         * @param cause
         */
        private MethodException(String message, Throwable cause) {
            super(message, cause);
        }

    }
}
