package de.pheru.media.core.util;

import java.text.DecimalFormat;

/**
 * Klasse zum formatieren von Bytes.
 */
public final class ByteUtil {

    public static final DecimalFormat TWO_DECIMAL_PLACES_FORMAT = new DecimalFormat("#0.00");
    public static final String UNIT_SIZE = " MB";

    private ByteUtil() {
        //Utility-Klasse
    }

    //TODO Test
    public static String bytesToMB(final long bytes) {
        final double mb = bytes / 1048576.0;
        return TWO_DECIMAL_PLACES_FORMAT.format(mb) + UNIT_SIZE;
    }
}
