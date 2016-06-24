package de.pheru.media.util;

import de.pheru.media.data.Mp3FileData;

import java.text.DecimalFormat;

/**
 * Klasse zum formatieren von Bytes.
 */
public final class ByteUtil {

    private static final DecimalFormat TWO_DECIMAL_PLACES_FORMAT = new DecimalFormat("#0.00");

    private ByteUtil() {
        //Utility-Klasse
    }

    /**
     * Wandelt einen Long mit Bytes in einen formatierten String mit MegaBytes.
     *
     * @param bytes Die zu formatierenden Bytes.
     * @return Einen String mit den Bytes in MegaBytes und angehängtem "MB";
     */
    public static String bytesToMB(Long bytes) {
        Double mb = Double.valueOf(bytes) / 1048576;
        return TWO_DECIMAL_PLACES_FORMAT.format(mb) + Mp3FileData.UNIT_SIZE;
    }

    /**
     * Wandelt einen String mit Bytes in einen formatierten String mit MegaBytes.
     *
     * @param bytes Die zu formatierenden Bytes.
     * @return Einen String mit den Bytes in MegaBytes und angehängtem "MB";
     */
    public static String bytesToMB(String bytes) {
        return bytesToMB(Long.valueOf(bytes));
    }
}
