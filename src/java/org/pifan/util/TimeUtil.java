/**************************************************************************
 *
 *  This program is an unpublished work fully protected by the United
 *  States copyright laws and is considered a trade secret belonging
 *  to Delcan. To the extent that this work may be considered "published,"
 *  the following notice applies:
 *
 *  "Copyright 2007-2015, Delcan, all rights  reserved."
 *
 *  Any unauthorized use, reproduction, distribution, display,
 *  modification, or disclosure of this program is strictly prohibited.
 *
 *************************************************************************/

package org.pifan.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.quartz.TimeOfDay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A time utility class.
 *
 * @author Dave Irwin (d.irwin@delcan.com)
 */
public final class TimeUtil {
    /**
     * 
     */
    private static final Logger logger = LoggerFactory.getLogger(TimeUtil.class);

    /**
     * 
     */
    public static final Long SECOND = new Long(1000);
    public static final Long MINUTE = new Long(1000 * 60);
    public static final Long HOUR = new Long(1000 * 60 * 60);
    public static final Long DAY = new Long(1000 * 60 * 60 * 24);

    public static enum TimeLabel {
        SECOND, MINUTE, HOUR, DAY;
    }

    private static final Map<TimeLabel, String> defaultTimeLabels = new HashMap<>();

    /**
     * 
     */
    private static final String singleDigitPattern = "0";
    private static final DecimalFormat singleDigitElapsedTimeFormatter = new DecimalFormat(singleDigitPattern);

    private static final String twoDigitPattern = "00";
    private static final DecimalFormat twoDigitElapsedTimeFormatter = new DecimalFormat(twoDigitPattern);

    private static final String msPattern = "000";
    private static final DecimalFormat elapsedMsFormatter = new DecimalFormat(msPattern);

    /**
     * 
     */
    public static final List<TimeOfDay> timeOfDayOptions = new ArrayList<>();

    static {
        for (int hour = 0; hour < 12; hour++) {
            for (int minute = 0; minute < 60; minute = minute + 30) {
                timeOfDayOptions.add(new TimeOfDay(hour, minute, 0));
            }
        }

        defaultTimeLabels.put(TimeLabel.DAY, "D");
        defaultTimeLabels.put(TimeLabel.HOUR, "H");
        defaultTimeLabels.put(TimeLabel.MINUTE, "M");
        defaultTimeLabels.put(TimeLabel.SECOND, "");
    }

    /**
     * Private non-instantiable constructor.
     */
    private TimeUtil() {
    }

    /**
     * Converts milliseconds into a readable time in the following format:
     *      XX days XX hours XX minutes XX seconds
     * 
     * @param startTime
     * @param endTime
     * @return The string formatted elapsed time
     */
    public static final String elapsedTime(long startTime, long endTime) {
        return elapsedTime(endTime - startTime);
    }

    /**
     * 
     * @param startTime
     * @param endTime
     * @param elapsedFormat
     * @return
     */
    public static final String elapsedTime(long startTime, long endTime, String elapsedFormat) {
        return elapsedTime(endTime - startTime, elapsedFormat);
    }

    /**
     * 
     * @param startTime
     * @param endTime
     * @return
     */
    public static final String elapsedTime(Date startTime, Date endTime) {
        if (startTime == null) {
            throw new IllegalArgumentException("Null start time!");
        }
        if (endTime == null) {
            throw new IllegalArgumentException("Null end time!");
        }

        return elapsedTime(endTime.getTime() - startTime.getTime());
    }

    /**
     * 
     * @param startTime
     * @param endTime
     * @param elapsedFormat
     * @return
     */
    public static final String elapsedTime(Date startTime, Date endTime, String elapsedFormat) {
        if (startTime == null) {
            throw new IllegalArgumentException("Null start time!");
        }
        if (endTime == null) {
            throw new IllegalArgumentException("Null end time!");
        }

        return elapsedTime(endTime.getTime() - startTime.getTime(), elapsedFormat, true);
    }

    /**
     * 
     * @param startTime
     * @param endTime
     * @param elapsedFormat
     * @param includeLabels
     * @return
     */
    public static final String elapsedTime(Date startTime, Date endTime, String elapsedFormat, Boolean includeLabels) {
        if (startTime == null) {
            throw new IllegalArgumentException("Null start time!");
        }
        if (endTime == null) {
            throw new IllegalArgumentException("Null end time!");
        }

        return elapsedTime(endTime.getTime() - startTime.getTime(), elapsedFormat, includeLabels);
    }

    /**
     * 
     * @param durationInMs
     * @return
     */
    public static final String elapsedTime(Long durationInMs) {
        return elapsedTime(durationInMs, "msS", true);
    }

    /**
     * 
     * @param durationInMs
     * @param elapsedFormat
     * @return
     */
    public static final String elapsedTime(Long durationInMs, String elapsedFormat) {
        return elapsedTime(durationInMs, elapsedFormat, true);
    }

    /**
     * 
     * @param durationInMs
     * @param elapsedFormat
     * @param includeLabels
     * @return
     */
    public static final String elapsedTime(Long durationInMs, String elapsedFormat, Boolean includeLabels) {
        return elapsedTime(durationInMs, elapsedFormat, includeLabels, true, defaultTimeLabels);
    }

    /**
     * 
     * @param durationInMs
     * @param elapsedFormat
     * @param includeLabels
     * @param includeLabelsForZeroValues
     * @return
     */
    public static final String elapsedTime(Long durationInMs, String elapsedFormat, Boolean includeLabels,
            Boolean includeLabelsForZeroValues) {
        return elapsedTime(durationInMs, elapsedFormat, includeLabels, includeLabelsForZeroValues, defaultTimeLabels);
    }

    /**
     * 
     * @param durationInMs
     * @param elapsedFormat
     * @param includeLabels
     * @param includeLabelsForZeroValues
     * @param customTimeLabels
     * @return
     */
    public static final String elapsedTime(Long durationInMs, String elapsedFormat, Boolean includeLabels,
            Boolean includeLabelsForZeroValues, Map<TimeLabel, String> customTimeLabels) {
        if (durationInMs == null) {
            return "N/A";
        }

        if (durationInMs < 0) {
            logger.warn("Duration is < 0...reseting to 0");
            durationInMs = 0L;
        }

        long seconds = 0;
        long minutes = 0;
        long hours = 0;
        long days = 0;

        // get the number of days
        days = durationInMs / DAY;
        durationInMs = durationInMs - (days * DAY);

        // get the number of hours
        hours = durationInMs / HOUR;
        durationInMs = durationInMs - (hours * HOUR);

        // get the number of minutes
        minutes = durationInMs / MINUTE;
        durationInMs = durationInMs - (minutes * MINUTE);

        // get the number of seconds
        seconds = durationInMs / SECOND;
        durationInMs = durationInMs - (seconds * SECOND);

        StringBuilder sb = new StringBuilder();

        boolean previousAdded = false;

        // check to add days
        if (elapsedFormat.contains("D")) {
            if (days > 0 || includeLabelsForZeroValues) {
                if (includeLabels) {
                    sb.append(singleDigitElapsedTimeFormatter.format(days));
                } else {
                    sb.append(twoDigitElapsedTimeFormatter.format(days));
                }

                if (includeLabels) {
                    String label = customTimeLabels.get(TimeLabel.DAY);
                    if (StringUtils.isValidString(label)) {
                        if (days == 1 && StringUtils.endsWith(label, "s", true)) {
                            String trimmedLabel = StringUtils.trimTrailingText(label.trim(), "s");

                            if (label.startsWith(" ")) {
                                sb.append(" ");
                            }
                            sb.append(trimmedLabel);
                            if (label.endsWith(" ")) {
                                sb.append(" ");
                            }
                        } else {
                            sb.append(label);
                        }
                    }
                }
                previousAdded = true;
            }
        }

        // check to add hours
        if (elapsedFormat.contains("H")) {
            if (previousAdded && !includeLabels) {
                sb.append(":");
            }

            if (hours > 0 || includeLabelsForZeroValues) {
                if (includeLabels) {
                    sb.append(singleDigitElapsedTimeFormatter.format(hours));
                } else {
                    sb.append(twoDigitElapsedTimeFormatter.format(hours));
                }

                if (includeLabels) {
                    String label = customTimeLabels.get(TimeLabel.HOUR);
                    if (StringUtils.isValidString(label)) {
                        if (hours == 1 && StringUtils.endsWith(label, "s", true)) {
                            String trimmedLabel = StringUtils.trimTrailingText(label.trim(), "s");

                            if (label.startsWith(" ")) {
                                sb.append(" ");
                            }
                            sb.append(trimmedLabel);
                            if (label.endsWith(" ")) {
                                sb.append(" ");
                            }
                        } else {
                            sb.append(label);
                        }
                    }
                }
                previousAdded = true;
            }
        }

        // check to add minutes
        if (elapsedFormat.contains("m")) {
            if (previousAdded && !includeLabels) {
                sb.append(":");
            }

            if (minutes > 0 || includeLabelsForZeroValues) {
                if (includeLabels) {
                    sb.append(singleDigitElapsedTimeFormatter.format(minutes));
                } else {
                    sb.append(twoDigitElapsedTimeFormatter.format(minutes));
                }

                if (includeLabels) {
                    String label = customTimeLabels.get(TimeLabel.MINUTE);
                    if (StringUtils.isValidString(label)) {
                        if (minutes == 1 && StringUtils.endsWith(label, "s", true)) {
                            String trimmedLabel = StringUtils.trimTrailingText(label.trim(), "s");

                            if (label.startsWith(" ")) {
                                sb.append(" ");
                            }
                            sb.append(trimmedLabel);
                            if (label.endsWith(" ")) {
                                sb.append(" ");
                            }
                        } else {
                            sb.append(label);
                        }
                    }
                }
                previousAdded = true;
            }
        }

        // check to add seconds
        if (elapsedFormat.contains("s")) {
            if (previousAdded && !includeLabels) {
                sb.append(":");
            }

            if (seconds > 0 || includeLabelsForZeroValues) {
                if (includeLabels) {
                    sb.append(singleDigitElapsedTimeFormatter.format(seconds));
                } else {
                    sb.append(twoDigitElapsedTimeFormatter.format(seconds));
                }

                if (includeLabels) {
                    String label = customTimeLabels.get(TimeLabel.SECOND);
                    if (StringUtils.isValidString(label)) {
                        if (seconds == 1 && StringUtils.endsWith(label, "s", true)) {
                            String trimmedLabel = StringUtils.trimTrailingText(label.trim(), "s");

                            if (label.startsWith(" ")) {
                                sb.append(" ");
                            }
                            sb.append(trimmedLabel);
                            if (label.endsWith(" ")) {
                                sb.append(" ");
                            }
                        } else {
                            sb.append(label);
                        }
                    }
                }
                previousAdded = true;
            }
        }

        // check to add milliseconds
        if (elapsedFormat.contains("S")) {
            if (durationInMs >= 0) {
                sb.append("," + elapsedMsFormatter.format(durationInMs));
            }
        }

        return sb.toString();
    }

    /**
     * 
     * @param hours
     * @return
     */
    public static String formatTime(float hours) {
        double minutes = Math.floor(60 * hours); // .round(60 *
        // averageTravelTime);
        double decimalSeconds = ((60 * hours) - minutes);
        long seconds = Math.round(60 * decimalSeconds);

        return Math.round(minutes) + ":"
                + (String.valueOf(seconds).length() == 1 ? "0" + String.valueOf(seconds) : String.valueOf(seconds));
    }

    /**
     * 
     * @param date
     * @param adjustmentInMs
     * @return
     */
    public static Date adjustDate(Date date, int adjustmentInMs) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.add(Calendar.MILLISECOND, adjustmentInMs);

        return cal.getTime();
    }

    /**
     * 
     * @param now
     * @param testDate
     * @return
     */
    public static boolean isWithinHour(Date now, Date testDate) {
        /*
         * The target drop date is before the current time but are we
         * within the same hour? 
         */
        Calendar c1 = Calendar.getInstance();
        c1.setTime(now);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);
        c1.set(Calendar.MILLISECOND, 0);
        Date nowAdjustedToHour = c1.getTime();

        Calendar c2 = Calendar.getInstance();
        c2.setTime(testDate);
        c2.set(Calendar.MINUTE, 0);
        c2.set(Calendar.SECOND, 0);
        c2.set(Calendar.MILLISECOND, 0);
        Date testDateAdjustedToHour = c2.getTime();

        if (nowAdjustedToHour.equals(testDateAdjustedToHour)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the current time in seconds between the current time and midnight, January 1, 1970 UTC.
     * 
     * @return
     */
    public static int getCurrentTimeInMin() {
        long currTimeInMs = System.currentTimeMillis();
        return convertToMin(currTimeInMs);
    }

    /**
     * Convert the time in milliseconds to days.
     * 
     * @param timeInMs
     * @return
     */
    public static int convertToDays(long timeInMs) {
        int currTimeInDays = (int) TimeUnit.DAYS.convert(timeInMs, TimeUnit.MILLISECONDS);
        return currTimeInDays;
    }

    /**
     * Convert the time in milliseconds to hours.
     * 
     * @param timeInMs
     * @return
     */
    public static int convertToHours(long timeInMs) {
        int currTimeInHours = (int) TimeUnit.HOURS.convert(timeInMs, TimeUnit.MILLISECONDS);
        return currTimeInHours;
    }

    /**
     * Convert the time in milliseconds to seconds.
     * 
     * @param timeInMs
     * @return
     */
    public static int convertToMin(long timeInMs) {
        int currTimeInSec = (int) (timeInMs / (1000 * 60));
        return currTimeInSec;
    }

    /**
     * 
     * @param timeInMin
     * @return
     */
    public static long convertToMs(int timeInMin) {
        long backToMin = ((long) timeInMin) * (1000 * 60);
        return backToMin;
    }

    /*
     * ===================================================================================
     * Time zone related utility methods
     * ===================================================================================
     */

    public static final TimeZone utcTZ = TimeZone.getTimeZone("UTC");

    /**
     * 
     * @param time
     * @param to
     * @return
     */
    public static long toLocalTime(long time, TimeZone to) {
        return convertTime(time, utcTZ, to);
    }

    /**
     * 
     * @param time
     * @param from
     * @return
     */
    public static long toUTC(long time, TimeZone from) {
        return convertTime(time, from, utcTZ);
    }

    /**
     * 
     * @param time
     * @param from
     * @param to
     * @return
     */
    public static long convertTime(long time, TimeZone from, TimeZone to) {
        return time + getTimeZoneOffset(time, from, to);
    }

    /**
     * 
     * @param time
     * @param from
     * @param to
     * @return
     */
    private static long getTimeZoneOffset(long time, TimeZone from, TimeZone to) {
        int fromOffset = from.getOffset(time);
        int toOffset = to.getOffset(time);
        int diff = 0;

        if (fromOffset >= 0) {
            if (toOffset > 0) {
                toOffset = -1 * toOffset;
            } else {
                toOffset = Math.abs(toOffset);
            }
            diff = (fromOffset + toOffset) * -1;
        } else {
            if (toOffset <= 0) {
                toOffset = -1 * Math.abs(toOffset);
            }
            diff = (Math.abs(fromOffset) + toOffset);
        }
        return diff;
    }
}