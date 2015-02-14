package de.eru.mp3manager.utils;

import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.utils.formatter.TimeFormatter;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

/**
 *
 * @author Philipp Bruckner
 */
public class Comparators {

    private Comparators() {
        //Utility-Klasse
    }

    /**
     * Ein Comparator für Zahlen.
     *
     */
    public static final Comparator<String> NUMBER_COMPARATOR = (String o1, String o2) -> {
        if (!isComparable(o1)) {
            return -1;
        }
        if (!isComparable(o2)) {
            return 1;
        }
        return Integer.compare(Integer.valueOf(o1), Integer.valueOf(o2));
    };

    /**
     * Ein Comparator für Daten.
     */
    public static final Comparator<String> DATE_COMPARATOR = (String o1, String o2) -> {
        if (!isComparable(o1)) {
            return -1;
        }
        if (!isComparable(o2)) {
            return 1;
        }
        try {
            Date d1 = TimeFormatter.DATE_TIME_FORMAT.parse(o1);
            Date d2 = TimeFormatter.DATE_TIME_FORMAT.parse(o2);
            return Long.compare(d1.getTime(), d2.getTime());
        } catch (ParseException ex) {
            ex.printStackTrace();
            return 0;
        }
    };

    /**
     * Ein Comparator für Dateigrößen.
     */
    public static final Comparator<String> SIZE_COMPARATOR = (String o1, String o2) -> {
        if (!isComparable(o1)) {
            return -1;
        }
        if (!isComparable(o2)) {
            return 1;
        }
        Double d1 = Double.valueOf(o1.replace(',', '.').replace(Mp3FileData.UNIT_SIZE, ""));
        Double d2 = Double.valueOf(o2.replace(',', '.').replace(Mp3FileData.UNIT_SIZE, ""));
        return Double.compare(d1, d2);
    };

    /**
     * Ein Comparator für Bitraten.
     */
    public static final Comparator<String> BITRATE_COMPARATOR = (String o1, String o2)
            -> {
                if (!isComparable(o1)) {
                    return -1;
                }
                if (!isComparable(o2)) {
                    return 1;
                }
                Double d1 = Double.valueOf(o1.replace(Mp3FileData.UNIT_BITRATE, ""));
                Double d2 = Double.valueOf(o2.replace(Mp3FileData.UNIT_BITRATE, ""));
                return Double.compare(d1, d2);
            };

    /**
     * Ein Comparator für Zeitangaben.
     */
    public static final Comparator<String> TIME_COMPARATOR = (String o1, String o2) -> {
        if (!isComparable(o1)) {
            return -1;
        }
        if (!isComparable(o2)) {
            return 1;
        }
        Date d1;
        Date d2;
        try {
            d1 = parseDuration(o1);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
        try {
            d2 = parseDuration(o2);
        } catch (ParseException e) {
            e.printStackTrace();
            return 1;
        }
        return Long.compare(d1.getTime(), d2.getTime());
    };

    /**
     * Parsed eine Zeitangabe im DURATION_FORMAT oder DURATION_WITH_HOURS_FORMAT
     * - Format in ein Date-Objekt.
     *
     * @param duration Die zu parsende Zeitangabe.
     * @return Das Date-Objekt zu der übergebenen Zeitangabe.
     * @throws ParseException Wenn die Zeitangabe weder im DURATION_FORMAT, noch
     * im DURATION_WITH_HOURS_FORMAT ist.
     */
    private static Date parseDuration(String duration) throws ParseException {
        Date d;
        try {
            d = TimeFormatter.DURATION_FORMAT.parse(duration);
        } catch (ParseException e) {
            d = TimeFormatter.DURATION_WITH_HOURS_FORMAT.parse(duration);
        }
        return d;
    }

    private static boolean isComparable(String s) {
        return (s != null && !s.isEmpty() && !s.equals(Mp3FileData.NOT_LOADED));
    }
}
