package de.eru.mp3manager.data;

import de.eru.mp3manager.utils.ExceptionHandler;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

/**
 *
 * @author Philipp Bruckner
 */
public class ArtworkData {

    private final ObjectProperty<byte[]> binaryData = new SimpleObjectProperty<>();
    private final IntegerProperty width = new SimpleIntegerProperty();
    private final IntegerProperty height = new SimpleIntegerProperty();
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
        if(width.get() <= 0.0){
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
        if(height.get() <= 0.0){
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
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(getBinaryData());
            ImageInputStream iis = ImageIO.createImageInputStream(bais);
            BufferedImage bi = ImageIO.read(iis);
            width.set(bi.getWidth());
            height.set(bi.getHeight());
        } catch (IOException ex) {
            ExceptionHandler.handle(ex);
        }
    }

}
