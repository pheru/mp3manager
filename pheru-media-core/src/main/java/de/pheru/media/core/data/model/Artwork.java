package de.pheru.media.core.data.model;

import java.util.Arrays;

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

    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof Artwork)) {
            return false;
        }
        if ( obj == this){
            return true;
        }
        final Artwork other = (Artwork) obj;
        return Arrays.equals(binaryData, other.getBinaryData());
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
