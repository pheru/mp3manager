package de.pheru.media.core.data.model;

public class Artwork {

    private byte[] binaryData;
    private int width;
    private int height;

    public Artwork() {
    }

    public Artwork(final byte[] binaryData, final int width, final int height) {
        this.binaryData = binaryData;
        this.width = width;
        this.height = height;
    }

    public byte[] getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(final byte[] binaryData) {
        this.binaryData = binaryData;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(final int height) {
        this.height = height;
    }
}
