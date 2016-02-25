package de.pheru.media.util;

import de.pheru.media.data.Mp3FileData;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;

/**
 * Klasse zum formatieren von Bytes.
 *
 * @author Philipp Bruckner
 */
public final class ByteUtil {

    private static final DecimalFormat TWO_DECIMAL_PLACES_FORMAT = new DecimalFormat("#0.00");

    private ByteUtil() {
        //Utility-Klasse
    }

    /**
     * Wandelt ein Byte-Array in ein Image.
     *
     * @param bytes Das Byte-Array des Images.
     * @return Das aus dem Byte-Array erzeugte Image.
     */
    public static Image byteArrayToImage(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("byte-array must not be null or empty!");
        }
        return new Image(new ByteArrayInputStream(bytes));
    }

    public static byte[] fileToByteArray(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
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
