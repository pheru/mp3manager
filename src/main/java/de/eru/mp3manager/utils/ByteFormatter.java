package de.eru.mp3manager.utils;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import javafx.scene.image.Image;

/**
 * Klasse zum formatieren von Bytes.
 *
 * @author Philipp Bruckner
 */
public final class ByteFormatter {

    private static final DecimalFormat TWO_DECIMAL_PLACES_FORMAT = new DecimalFormat("#0.00");
    private final static Image NO_IMAGE_AVAILABLE = new Image("img/noImage.png");

    private ByteFormatter() {
        //Utility-Klasse
    }

    /**
     * Wandelt ein Byte-Array in ein Image.
     *
     * @param bytes Das Byte-Array des Images.
     * @return Das aus dem Byte-Array erzeugte Image.
     */
    public static Image byteArrayToImage(byte[] bytes) { //TODO In der richtigen Klasse?
        if (bytes != null && bytes.length > 0) {
            return new Image(new ByteArrayInputStream(bytes));
        }
        return NO_IMAGE_AVAILABLE;
    }

    /**
     * Wandelt einen Long mit Bytes in einen formatierten String mit MegaBytes.
     *
     * @param bytes Die zu formatierenden Bytes.
     * @return Einen String mit den Bytes in MegaBytes und angeh�ngtem "MB";
     */
    public static String bytesToMB(Long bytes) {
        Double mb = Double.valueOf(bytes) / 1048576;
        return TWO_DECIMAL_PLACES_FORMAT.format(mb) + " MB";
    }

    /**
     * Wandelt einen String mit Bytes in einen formatierten String mit MegaBytes.
     *
     * @param bytes Die zu formatierenden Bytes.
     * @return Einen String mit den Bytes in MegaBytes und angeh�ngtem "MB";
     */
    public static String bytesToMB(String bytes) {
        return bytesToMB(Long.valueOf(bytes));
    }
}
