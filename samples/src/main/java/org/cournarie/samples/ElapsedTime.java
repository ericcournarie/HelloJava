/*
 * Copyright Eric Cournarie 2017
 */
package org.cournarie.samples;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Duration pretty printer.
 *
 * @author ecournarie
 */
public final class ElapsedTime {

    /** Milliseconds to seconds. */
    private static final int T1000 = 1000;
    /** More than 90 seconds. */
    private static final int T90 = 90;
    /** Seconds to minutes. */
    private static final int T60 = 60;
    /** Less than five seconds. */
    private static final int T5 = 5;
    /** Number formatter for hours, minutes, seconds. */
    private static final NumberFormat DOUBLE_FMT2 = new DecimalFormat("00");
    /** Number formatter for milliseconds. */
    private static final NumberFormat DOUBLE_FMT3 = new DecimalFormat("000");

    private ElapsedTime() {
    }

    /**
     * Returns elapsed time in a pretty-print format.
     *
     * @param milli number of milliseconds
     * @return a text representation of the time
     */
    public static String format(long milli) {
        StringBuilder buf = new StringBuilder();
        long milliseconds = milli;
        long seconds = milliseconds / T1000;
        if (seconds < T5) {
            buf.append(Long.toString(milliseconds));
            buf.append(" milliseconds");
        } else {
            if (seconds < T90) {
                buf.append(seconds);
                buf.append(" second");
                if (seconds > 1) {
                    buf.append("s");
                }
            } else {
                long minutes = seconds / T60;
                if (minutes < T90) {
                    formatMinutes(buf, minutes, seconds);
                } else {
                    formatHours(buf, minutes);
                }
            }
        }
        return buf.toString();
    }

    /**
     * Returns elapsed time in a nice but shorter format.
     *
     * @param milliseconds
     * @return a text representation of the time
     */
    public static String shortFormat(long milliseconds) {
        StringBuilder buf = new StringBuilder();
        long seconds = milliseconds / T1000;
        if (seconds < T5) {
            buf.append(Long.toString(milliseconds));
            buf.append("ms");
        } else {
            if (seconds < T90) {
                buf.append(seconds);
                buf.append("s");
            } else {
                long minutes = seconds / T60;
                if (minutes < T90) {
                    shortMinuteFormat(buf, minutes, seconds);
                } else {
                    shortHourFormat(buf, minutes);
                }
            }
        }
        return buf.toString();
    }

    /**
     * Short minutes format.
     * @param buf
     * @param minutes
     * @param seconds
     */
    private static void shortMinuteFormat(StringBuilder buf, long minutes, long seconds) {
        long sec = seconds;
        buf.append(minutes);
        buf.append("mn");
        sec %= T60;
        if (sec > 0) {
            buf.append(", ");
            buf.append(sec);
            buf.append("s");
        }
    }

    /**
     * Hour format in short form.
     * @param buf
     * @param minutes
     */
    private static void shortHourFormat(StringBuilder buf, long minutes) {
        long min = minutes;
        long hours = minutes / T60;
        buf.append(hours);
        buf.append("h");
        min %= T60;
        if (min > 0) {
            buf.append(", ");
            buf.append(min);
            buf.append("mn");
        }
    }

    /**
     * Returns elapse time in a nice clock format.
     *
     * @param millisecond
     * @return a text representation of the time
     */
    public static String clockFormat(long millisecond) {
        return clockFormat(millisecond, true);
    }

    /**
     * Returns elapse time in a nice clock format.
     *
     * @param millisecond
     * @param showMilli
     * @return a text representation of the time
     */
    public static String clockFormat(long millisecond, boolean showMilli) {
        long milliseconds = millisecond;
        StringBuilder buf = new StringBuilder();
        long seconds = milliseconds / T1000;
        milliseconds = milliseconds - (seconds * T1000);
        long minutes = seconds / T60;
        seconds = seconds - (minutes * T60);
        long hours = minutes / T60;
        minutes = minutes - (hours * T60);
        if (hours > 0) {
            String sHours = DOUBLE_FMT2.format(hours);
            buf.append(sHours).append(":");
        }
        String sMinutes = DOUBLE_FMT2.format(minutes);
        buf.append(sMinutes).append(":");
        String sSeconds = DOUBLE_FMT2.format(seconds);
        buf.append(sSeconds);
        String sMillis = DOUBLE_FMT3.format(milliseconds);
        if (showMilli) {
            buf.append(":").append(sMillis);
        }
        return buf.toString();
    }

    /**
     * Formats minutes.
     * @param buf the string buffer to write in
     * @param minutes number of minutes
     * @param seconds number of seconds
     */
    private static void formatMinutes(StringBuilder buf, long minutes, long seconds) {
        long secs = seconds;
        buf.append(minutes);
        buf.append(" minute");
        if (minutes != 1) {
            buf.append("s");
        }
        secs %= T60;
        if (secs != 1) {
            buf.append(", ");
            buf.append(secs);
            buf.append(" second");
            if (secs != 1) {
                buf.append("s");
            }
        }
    }

    /**
     * Format hours.
     * @param buf the string buffer to write in
     * @param minutes number of minutes
     */
    private static void formatHours(StringBuilder buf, long minutes) {
        long mins = minutes;
        long hours = mins / T60;
        buf.append(hours);
        buf.append(" hour");
        if (hours != 1) {
            buf.append("s");
        }
        mins %= T60;
        if (mins != 0) {
            buf.append(", ");
            buf.append(mins);
            buf.append(" minute");
            if (mins != 1) {
                buf.append("s");
            }
        }
    }

}
