package de.eru.mp3manager.utils;

import de.eru.mp3manager.data.Mp3FileData;
import de.eru.mp3manager.utils.formatter.TimeFormatter;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

/**
 *
 * @author Philipp Bruckner
 */
public final class Comparators implements Serializable{

    public static final Comparator<String> NUMBER_COMPARATOR = createNumberComparator();
    public static final Comparator<String> DATE_COMPARATOR = createDateComparator();
    public static final Comparator<String> SIZE_COMPARATOR = createSizeComparator();
    public static final Comparator<String> BITRATE_COMPARATOR = createBitrateComparator();
    public static final Comparator<String> TIME_COMPARATOR = createTimeComparator();

    private Comparators() {
        //Utility-Klasse
    }

    /**
     * @return Einen Comparator für Zahlen.
     */
    private static Comparator<String> createNumberComparator() {
        return (String o1, String o2) -> {
            if (!isComparable(o1)) {
                return -1;
            }
            if (!isComparable(o2)) {
                return 1;
            }
            return Integer.compare(Integer.valueOf(o1), Integer.valueOf(o2));
        };
    }

    /**
     * @return Einen Comparator für Daten.
     */
    private static Comparator<String> createDateComparator() {
        return (String o1, String o2) -> {
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
    }

    /**
     * @return Einen Comparator für Dateigrößen.
     */
    private static Comparator<String> createSizeComparator() {
        return (String o1, String o2) -> {
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
    }

    /**
     * @return Einen Comparator für Bitraten.
     */
    private static Comparator<String> createBitrateComparator() {
        return (String o1, String o2)
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
    }

    /**
     * @return Einen Comparator für Zeitangaben.
     */
    private static Comparator<String> createTimeComparator() {
        return (String o1, String o2) -> {
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
    }

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
        return s != null && !s.isEmpty();
    }
}
