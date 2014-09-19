package de.eru.mp3manager.utils.formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Klasse zum formatieren von Datums- und Zeitangaben.
 *
 * @author Philipp Bruckner
 */
public final class TimeFormatter {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");
    public static final SimpleDateFormat DURATION_WITH_HOURS_FORMAT = new SimpleDateFormat("HH:mm:ss");
    public static final SimpleDateFormat DURATION_FORMAT = new SimpleDateFormat("mm:ss");

    private TimeFormatter() {
        //Utility-Klasse
    }

    /**
     * Wandelt einen double in einen String mit HH:mm:ss oder mm:ss Format.
     *
     * @param seconds Die zu formatierenden Sekunden.
     * @param withHours true, wenn HH:mm:ss Format. false für mm:ss.
     * @return String im HH:mm:ss oder mm:ss Format.
     */
    public static String secondsToDurationFormat(double seconds, boolean withHours) {
        SimpleDateFormat smf;
        if (seconds >= 3600 || withHours) {
            smf = DURATION_WITH_HOURS_FORMAT;
        } else {
            smf = DURATION_FORMAT;
        }
        try {
            Date date = new SimpleDateFormat("s").parse(String.valueOf(seconds));
            return smf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Wandelt einen Long in einen String mit HH:mm:ss oder mm:ss Format.
     *
     * @param seconds Die zu formatierenden Sekunden.
     * @param withHours true, wenn HH:mm:ss Format. false für mm:ss.
     * @return String im HH:mm:ss oder mm:ss Format.
     */
    public static String secondsToDurationFormat(Long seconds, boolean withHours) {
        return secondsToDurationFormat(seconds.doubleValue(), withHours);
    }

    /**
     * Wandelt einen String in einen String mit HH:mm:ss oder mm:ss Format.
     *
     * @param seconds Die zu formatierenden Sekunden.
     * @param withHours true, wenn HH:mm:ss Format. false für mm:ss.
     * @return String im HH:mm:ss oder mm:ss Format.
     */
    public static String secondsToDurationFormat(String seconds, boolean withHours) {
        return secondsToDurationFormat(Double.valueOf(seconds), withHours);
    }

    /**
     * Wandelt einen double in einen String mit HH:mm:ss oder mm:ss Format.
     *
     * @param milliseconds Die zu formatierenden Millisekunden.
     * @param withHours true, wenn HH:mm:ss Format. false für mm:ss.
     * @return String im HH:mm:ss oder mm:ss Format.
     */
    public static String millisecondsToDurationFormat(double milliseconds, boolean withHours) {
        SimpleDateFormat smf;
        if (milliseconds >= 3600000 || withHours) {
            smf = DURATION_WITH_HOURS_FORMAT;
        } else {
            smf = DURATION_FORMAT;
        }
        try {
            Date date = new SimpleDateFormat("S").parse(String.valueOf(milliseconds));
            return smf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Wandelt einen Long in einen String mit HH:mm:ss oder mm:ss Format.
     *
     * @param milliseconds Die zu formatierenden Millisekunden.
     * @param withHours true, wenn HH:mm:ss Format. false für mm:ss.
     * @return String im HH:mm:ss oder mm:ss Format.
     */
    public static String millisecondsToDurationFormat(Long milliseconds, boolean withHours) {
        return millisecondsToDurationFormat(milliseconds.doubleValue(), withHours);
    }

    /**
     * Wandelt einen String in einen String mit HH:mm:ss oder mm:ss Format.
     *
     * @param milliseconds Die zu formatierenden Millisekunden.
     * @param withHours true, wenn HH:mm:ss Format. false für mm:ss.
     * @return String im HH:mm:ss oder mm:ss Format.
     */
    public static String millisecondsToDurationFormat(String milliseconds, boolean withHours) {
        return millisecondsToDurationFormat(Double.valueOf(milliseconds), withHours);
    }

    /**
     * Wandelt Millisekunden in einen String mit dd.MM.yyyy Format.
     *
     * @param milliseconds Die zu formatierenden Millisekunden
     * @return Ein String im dd.MM.yyyy Format.
     */
    public static String millisecondsToDateFormat(Long milliseconds) {
        Date date = new Date(milliseconds);
        return DATE_TIME_FORMAT.format(date);
    }
}
