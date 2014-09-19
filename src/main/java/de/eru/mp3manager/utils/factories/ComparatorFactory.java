package de.eru.mp3manager.utils.factories;

import de.eru.mp3manager.utils.formatter.TimeFormatter;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

/**
 * Klasse zum erzeugen von Comparators.
 *
 * @author Philipp Bruckner
 */
public final class ComparatorFactory {

    private ComparatorFactory() {
        //Utility-Klasse
    }

    /**
     * Erzeugt einen Comparator für Zahlen.
     *
     * @return Ein Comparator für Zahlen.
     */
    public static Comparator<String> createNumberComparator() {
        return (String o1, String o2) -> {
            if (o1 == null || o1.isEmpty()) {
                return -1;
            } else if (o2 == null || o2.isEmpty()) {
                return 1;
            }
            return Integer.compare(Integer.valueOf(o1), Integer.valueOf(o2));
        };
    }

    /**
     * Erzeugt einen Comparator für Daten.
     *
     * @return Einen Comparator für Daten.
     */
    public static Comparator<String> createDateComparator() {
        return (String o1, String o2) -> {
            if (o1 == null || o1.isEmpty()) {
                return -1;
            } else if (o2 == null || o2.isEmpty()) {
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
     * Erzeugt einen Comparator für Dateigrößen.
     *
     * @return Einen Comparator für Dateigrößen.
     */
    public static Comparator<String> createSizeComparator() {
        return (String o1, String o2) -> {
            if (o1 == null || o1.isEmpty()) {
                return -1;
            } else if (o2 == null || o2.isEmpty()) {
                return 1;
            }
            Double d1 = Double.valueOf(o1.replace(',', '.').replace(" MB", ""));
            Double d2 = Double.valueOf(o2.replace(',', '.').replace(" MB", ""));
            return Double.compare(d1, d2);
        };
    }

    /**
     * Erzeugt einen Comparator für Zeitangaben.
     *
     * @return Einen Comparator für Zeitangaben.
     */
    public static Comparator<String> createTimeComparator() {
        return (String o1, String o2) -> {
            if (o1 == null || o1.isEmpty()) {
                return -1;
            } else if (o2 == null || o2.isEmpty()) {
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
     * Parsed eine Zeitangabe im DURATION_FORMAT oder DURATION_WITH_HOURS_FORMAT - Format in ein Date-Objekt.
     *
     * @param duration Die zu parsende Zeitangabe.
     * @return Das Date-Objekt zu der übergebenen Zeitangabe.
     * @throws ParseException Wenn die Zeitangabe weder im DURATION_FORMAT, noch im DURATION_WITH_HOURS_FORMAT ist.
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

}
