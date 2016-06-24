package de.pheru.media.data;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Arrays;

/**
 * Enthält Informationen über das Artwork einer {@link Mp3FileData}.
 */
public class ArtworkData {

    private final ObjectProperty<byte[]> binaryData = new SimpleObjectProperty<>(new byte[0]);
    private final IntegerProperty width = new SimpleIntegerProperty(0);
    private final IntegerProperty height = new SimpleIntegerProperty(0);
    private final StringProperty mimeType = new SimpleStringProperty("");

    public ArtworkData(byte[] binaryData, int width, int height, String mimeType) {
        this.binaryData.set(binaryData);
        this.width.set(width);
        this.height.set(height);
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
        return width.get();
    }

    public void setWidth(final Integer width) {
        this.width.set(width);
    }

    public IntegerProperty widthProperty() {
        return width;
    }

    public Integer getHeight() {
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

    public static boolean equals(ArtworkData a1, ArtworkData a2) {
        if (a1 == a2) {
            return true;
        }
        if (a1 == null || a2 == null) {
            return false;
        }
        return Arrays.equals(a1.getBinaryData(), a2.getBinaryData());
    }

}
