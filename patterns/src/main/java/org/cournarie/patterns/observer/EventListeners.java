/*
 * Copyright Eric Cournarie 2017
 */
package org.cournarie.patterns.observer;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Event listeners.
 * @author eric
 */
public class EventListeners {

    /** Listener list. */
    private final List<EventCallback> listeners = new LinkedList<>();

    /**
     * Creates a new event listeners.
     */
    public EventListeners() {
        // empty constructor
    }

    /**
     * Adds a new listener.
     * @param eventType
     * @param object
     * @param method
     */
    public void addListener(Class<? extends EventObject> eventType, Object object, Method method) {
        synchronized (listeners) {
            listeners.add(new EventCallback(eventType, object, method));
        }
    }

    /**
     * Removes a listener.
     * @param eventType
     * @param target
     * @param method
     */
    public void removeListener(Class<?> eventType, Object target, Method method) {
        synchronized (listeners) {
            final Iterator<EventCallback> i = listeners.iterator();
            while (i.hasNext()) {
                final EventCallback lm = i.next();
                if (lm.match(eventType, target, method)) {
                    i.remove();
                }
            }
        }
    }

    /**
     * Fires an event.
     * @param event
     */
    public void fireEvent(EventObject<?> event) {
        // protect against concurrent notifications
        // on event, a remove or add is possible
        List<EventCallback> callings;
        synchronized (listeners) {
            callings = new LinkedList<>(listeners);
        }
        callings.forEach(callback -> callback.sendEvent(event));
    }
}
