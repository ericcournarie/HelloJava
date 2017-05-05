/*
 * Copyright Eric Cournarie 2017
 */
package org.cournarie.patterns;

import org.apache.log4j.Logger;

/**
 * A singleton pattern with lazy instantiation.
 * @author eric
 */
public class Singleton {

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(Singleton.class);

    /**
     * Hidden constructor.
     */
    private Singleton() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Singleton instatiation.");
        }
    }

    /** *
     * Get the singleton instance.
     * @return the singleton instance
     */
    public static Singleton getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Lazy evaluation pattern.
     */
    private static class SingletonHolder {

        /** The singleton instance. */
        private static final Singleton INSTANCE = new Singleton();

        /**
         * Hidden constructor.
         */
        private SingletonHolder() {
        }
    }
}
