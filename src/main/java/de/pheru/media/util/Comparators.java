package de.pheru.media.util;

import de.pheru.media.data.Mp3FileData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

public final class Comparators {

    public static final Comparator<String> NUMBER_COMPARATOR = createNumberComparator();
    public static final Comparator<String> DATE_COMPARATOR = createDateComparator();
    public static final Comparator<String> SIZE_COMPARATOR = createSizeComparator();
    public static final Comparator<String> BITRATE_COMPARATOR = createBitrateComparator();
    public static final Comparator<String> TIME_COMPARATOR = createTimeComparator();

    private static final Logger LOGGER = LogManager.getLogger(Comparators.class);
    
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
            Date d1;
            Date d2;
            if (!isComparable(o1)) {
                return -1;
            }
            try {
                d1 = TimeUtil.DATE_TIME_FORMAT.parse(o1);
            } catch (ParseException e) {
                LOGGER.warn("Exception parsing" + o1 + " to date!", e);
                return -1;
            }
            if (!isComparable(o2)) {
                return 1;
            }
            try {
                d2 = TimeUtil.DATE_TIME_FORMAT.parse(o2);
            } catch (ParseException e) {
                LOGGER.warn("Exception parsing" + o2 + " to date!", e);
                return 1;
            }
            return Long.compare(d1.getTime(), d2.getTime());
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
            Date d1;
            Date d2;
            if (!isComparable(o1)) {
                return -1;
            }
            try {
                d1 = parseDuration(o1);
            } catch (ParseException e) {
                LOGGER.warn("Exception parsing " + o1 + " to duration!", e);
                return -1;
            }
            if (!isComparable(o2)) {
                return 1;
            }
            try {
                d2 = parseDuration(o2);
            } catch (ParseException e) {
                LOGGER.warn("Exception parsing " + o2 + " to duration!", e);
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
            d = TimeUtil.DURATION_FORMAT.parse(duration);
        } catch (ParseException e) {
            d = TimeUtil.DURATION_WITH_HOURS_FORMAT.parse(duration);
        }
        return d;
    }

    private static boolean isComparable(String s) {
        return s != null && !s.isEmpty();
    }
}
