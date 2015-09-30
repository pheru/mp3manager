package de.pheru.media.data;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Philipp Bruckner
 */
public class ArtworkData {

    private static final Logger LOGGER = LogManager.getLogger(ArtworkData.class);

    private final ObjectProperty<byte[]> binaryData = new SimpleObjectProperty<>();
    private final IntegerProperty width = new SimpleIntegerProperty(0);
    private final IntegerProperty height = new SimpleIntegerProperty(0);
    private final StringProperty mimeType = new SimpleStringProperty();

    public ArtworkData(byte[] binaryData, int width, int height, String mimeType) {
        this.binaryData.set(binaryData);
        this.width.set(width);
        this.height.set(height);
        this.mimeType.set(mimeType);
    }

    public ArtworkData(byte[] binaryData, String mimeType) {
        this.binaryData.set(binaryData);
        this.mimeType.set(mimeType);
    }

    public byte[] getBinaryData() {
        return binaryData.get();
    }

    public void setBinaryData(final byte[] binaryData) {
        this.binaryData.set(binaryData);
    }

    public ObjectProperty<byte[]> binaryDataProperty() {
        return binaryData;
    }

    public Integer getWidth() {
        if (width.get() == 0) {
            loadDimensions();
        }
        return width.get();
    }

    public void setWidth(final Integer width) {
        this.width.set(width);
    }

    public IntegerProperty widthProperty() {
        return width;
    }

    public Integer getHeight() {
        if (height.get() == 0) {
            loadDimensions();
        }
        return height.get();
    }

    public void setHeight(final Integer height) {
        this.height.set(height);
    }

    public IntegerProperty heightProperty() {
        return height;
    }

    public String getMimeType() {
        return mimeType.get();
    }

    public void setMimeType(final String mimeType) {
        this.mimeType.set(mimeType);
    }

    public StringProperty mimeTypeProperty() {
        return mimeType;
    }

    private void loadDimensions() {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(getBinaryData())) {
            BufferedImage bi = ImageIO.read(bais);
            width.set(bi.getWidth());
            height.set(bi.getHeight());
        } catch (IOException e) {
            LOGGER.error("Exception loading dimensions of ArtworkData!", e);
            width.set(-1);
            height.set(-1);
        }
    }

    @SuppressWarnings("null") //null-prüfung findet statt
    public static boolean equals(ArtworkData a1, ArtworkData a2) {
        if ((a1 == null && a2 != null)
                || (a2 == null && a1 != null)) {
            return false;
        }
        if (a1 == null && a2 == null) {
            return true;
        }
        return Arrays.equals(a1.getBinaryData(), a2.getBinaryData());
    }

}
