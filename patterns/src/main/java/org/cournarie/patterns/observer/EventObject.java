/*
 * Copyright Eric Cournarie 2017
 */
package org.cournarie.patterns.observer;

/**
 * This EventObject is just a template version of the java.util one.
 * @param <T>
 */
public class EventObject<T> {

    /** The object on which the event occurred. */
    protected T source;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the event occurred.
     * @exception IllegalArgumentException if source is null.
     */
    public EventObject(T source) {
        if (source == null) {
            throw new IllegalArgumentException("Null source");
        }
        this.source = source;
    }

    /**
     * The object on which the event occurred.
     *
     * @return The object on which the event occurred.
     */
    public T getSource() {
        return source;
    }

    /**
     * Returns a string representation of this EventObject.
     *
     * @return a String representation of this EventObject.
     */
    @Override
    public String toString() {
        return getClass().getName() + "[source=" + source + "]";
    }
}
