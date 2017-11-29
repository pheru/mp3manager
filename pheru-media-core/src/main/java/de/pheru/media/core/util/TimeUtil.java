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

    public static final SimpleDateFormat DURATION_FORMAT = new SimpleDateFormat("mm:ss");
    public static final SimpleDateFormat DURATION_WITH_HOURS_FORMAT = new SimpleDateFormat("HH:mm:ss");

    private static final Logger LOGGER = LogManager.getLogger(TimeUtil.class);

    private TimeUtil() {
        //Utility-Klasse
    }

    //TODO Test
    public static String secondsToDurationFormat(final int seconds) {
        final DateFormat smf;
        if (seconds >= 3600) {
            smf = DURATION_WITH_HOURS_FORMAT;
        } else {
            smf = DURATION_FORMAT;
        }
        try {
            final Date date = new SimpleDateFormat("s").parse(String.valueOf(seconds));
            return smf.format(date);
        } catch (final ParseException e) {
            LOGGER.error("Exception parsing " + seconds + " to duration-format!", e);
            return "error";
        }
    }

}
