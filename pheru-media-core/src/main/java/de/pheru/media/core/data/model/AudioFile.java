package de.pheru.media.core.data.model;

public class AudioFile extends BaseFile {

    private String title;
    private String album;
    private String artist;
    private String genre;

    private int duration;
    private short track;
    private short year;
    private short bitrate;

    private long size;

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(final String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(final String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(final String genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(final int duration) {
        this.duration = duration;
    }

    public short getTrack() {
        return track;
    }

    public void setTrack(final short track) {
        this.track = track;
    }

    public short getYear() {
        return year;
    }

    public void setYear(final short year) {
        this.year = year;
    }

    public short getBitrate() {
        return bitrate;
    }

    public void setBitrate(final short bitrate) {
        this.bitrate = bitrate;
    }

    public long getSize() {
        return size;
    }

    public void setSize(final long size) {
        this.size = size;
    }
}
