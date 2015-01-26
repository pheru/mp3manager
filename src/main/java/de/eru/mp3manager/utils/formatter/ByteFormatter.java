package de.eru.mp3manager.utils.formatter;

import de.eru.mp3manager.data.Mp3FileData;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import javafx.scene.image.Image;

/**
 * Klasse zum formatieren von Bytes.
 *
 * @author Philipp Bruckner
 */
public final class ByteFormatter {

    private static final DecimalFormat TWO_DECIMAL_PLACES_FORMAT = new DecimalFormat("#0.00");
    private static final Image NO_IMAGE_AVAILABLE = new Image("img/noImage.png");

    private ByteFormatter() {
        //Utility-Klasse
    }

    /**
     * Wandelt ein Byte-Array in ein Image.
     *
     * @param bytes Das Byte-Array des Images.
     * @return Das aus dem Byte-Array erzeugte Image.
     */
    public static Image byteArrayToImage(byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            return new Image(new ByteArrayInputStream(bytes));
        }
        return NO_IMAGE_AVAILABLE;
    }
    
    public static byte[] fileToByteArray(File file){
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            byte[] byteArray = new byte[(int) randomAccessFile.length()];
            randomAccessFile.read(byteArray);
            return byteArray;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
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
