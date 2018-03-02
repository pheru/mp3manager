package de.pheru.media.core.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Klasse zum formatieren von Datums- und Zeitangaben.
 */
public final class TimeUtil {

    private static final Logger LOGGER = LogManager.getLogger(TimeUtil.class);

    public static final SimpleDateFormat DURATION_FORMAT = new SimpleDateFormat("mm:ss");
    public static final SimpleDateFormat DURATION_WITH_HOURS_FORMAT = new SimpleDateFormat("HH:mm:ss");
    public static final int SECONDS_PER_DAY = 86400;
    public static final int SECONDS_PER_HOUR = 3600;

    private TimeUtil() {
        //Utility-Klasse
    }

    public static String secondsToDurationFormat(final int seconds) {
        final DateFormat durationFormat;
        if (seconds >= SECONDS_PER_DAY) {
            return "> 23:59:59";
        } else if (seconds >= SECONDS_PER_HOUR) {
            durationFormat = DURATION_WITH_HOURS_FORMAT;
        } else {
            durationFormat = DURATION_FORMAT;
        }
        try {
            final Date date = new SimpleDateFormat("s").parse(String.valueOf(seconds));
            return durationFormat.format(date);
        } catch (final ParseException e) {
            LOGGER.error("Exception parsing " + seconds + " to duration-format!", e);
            return "error";
        }
    }

}
