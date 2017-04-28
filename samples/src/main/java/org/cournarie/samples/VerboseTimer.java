/*
 * Copyright Eric Cournarie 2017
 */
package org.cournarie.samples;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Timer with pretty printing.
 */
public class VerboseTimer {

    /** Milliseconds. */
    private static final double T1000 = 1000.;
    /** A double formatter. */
    private static final NumberFormat DOUBLE_FMT3 = new DecimalFormat("#.###");
    /** Start time. */
    private long startTime;
    /** Stop time. */
    private long stopTime;
    /** A boolean representing whether the timer is currently running or not. */
    private boolean isRunning;

    /**
     * Constructor.
     */
    public VerboseTimer() {
        startTime = 0;
        stopTime = 0;
        isRunning = false;
    }

    /**
     * Returns a string representation of a double value.
     *
     * @param d the value
     * @return the string representation
     */
    public static String stringify(double d) {
        return DOUBLE_FMT3.format(d);
    }

    /**
     * Starts the timer.
     */
    public void start() {
        startTime = System.currentTimeMillis();
        isRunning = true;
    }

    /**
     * Stops the timer.
     */
    public void stop() {
        stopTime = System.currentTimeMillis();
        isRunning = false;
    }

    /**
     * Prints elapsed time in a pretty manner.
     *
     * @return string representation of the elapsed time
     */
    public String format() {
        return ElapsedTime.format(stopTime - startTime);
    }

    /**
     * Prints elapsed time in a pretty manner.
     *
     * @return string representation of the elapsed time
     */
    public String shortFormat() {
        return ElapsedTime.shortFormat(stopTime - startTime);
    }

    /**
     * Prints elapsed time in a pretty manner.
     *
     * @return string representation of the elapse time
     */
    public String clockFormat() {
        return ElapsedTime.clockFormat(stopTime - startTime);
    }

    /**
     * Gets the elapsed time in milliseconds.
     *
     * @return the time in milliseconds
     */
    public long elapseTime() {
        long stop = stopTime;
        if (stop == 0) {
            stop = System.currentTimeMillis();
        }
        return stop - startTime;
    }

    /**
     * Resets the timer.
     */
    public void reset() {
        start();
        stopTime = 0;
    }

    /**
     * Gets the elapse time in seconds.
     *
     * @return the time in seconds
     */
    public double elapse() {
        return elapseTime() / T1000;
    }

    /**
     * @see Object#toString()
     * @return a string representation of the elapsed time
     */
    @Override
    public String toString() {
        return stringify(elapse());
    }

    /**
     * Return a boolean representing whether this timer is currently running or not.
     *
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return isRunning;
    }
}
