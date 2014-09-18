package de.eru.mp3manager.data;

import de.eru.mp3manager.utils.ByteFormatter;
import de.eru.mp3manager.utils.TimeFormatter;
import java.io.File;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Datenmodell für die Mp3-Dateien.
 *
 * @author Philipp Bruckner
 */
public class Mp3FileData {

    private static final String NOT_LOADED = "<Nicht geladen>";

    private final StringProperty fileName = new SimpleStringProperty(NOT_LOADED);
    private final StringProperty filePath = new SimpleStringProperty(NOT_LOADED);
    private final StringProperty absolutePath = new SimpleStringProperty(NOT_LOADED);
    private final StringProperty title = new SimpleStringProperty(NOT_LOADED);
    private final StringProperty album = new SimpleStringProperty(NOT_LOADED);
    private final StringProperty artist = new SimpleStringProperty(NOT_LOADED);
    private final StringProperty genre = new SimpleStringProperty(NOT_LOADED);
    private final StringProperty track = new SimpleStringProperty(NOT_LOADED);
    private final StringProperty year = new SimpleStringProperty(NOT_LOADED);
    private final StringProperty size = new SimpleStringProperty(NOT_LOADED);
    private final StringProperty lastModified = new SimpleStringProperty(NOT_LOADED);
    private final ObjectProperty<byte[]> cover = new SimpleObjectProperty<>();
    private final DoubleProperty duration = new SimpleDoubleProperty(0);
    private final StringProperty formattedDuration = new SimpleStringProperty(NOT_LOADED);
    private final BooleanProperty currentlyPlayed = new SimpleBooleanProperty(false);
    private final BooleanProperty loaded = new SimpleBooleanProperty(false);

    /**
     * Erstellt ein leeres Mp3FileData-Objekt.
     */
    public Mp3FileData() {
        absolutePath.bind(filePath.concat("\\").concat(fileName));
        formattedDuration.bind(new StringBinding() {
            {
                bind(duration);
            }

            @Override
            protected String computeValue() {
                return TimeFormatter.secondsToDurationFormat(duration.get(), false);
            }
        });
    }

    /**
     * Erstellt ein Mp3FileData-Objekt mit allgemeinen File-Informationen.
     *
     * @param file Die Datei für den Pfad.
     */
    public Mp3FileData(File file) {
        this();
        fileName.set(file.getName());
        filePath.set(file.getParent());
        size.set(ByteFormatter.bytesToMB(file.length()));
    }

    /**
     * Aktualisiert das Mp3FileData-Objekt.
     *
     * @param mp3FileData Das Mp3FileData-Objekt mit den neuen Informationen.
     */
    public void update(Mp3FileData mp3FileData) { //TODO Nicht benutzt
        setAlbum(mp3FileData.getAlbum());
        setArtist(mp3FileData.getArtist());
        setCover(mp3FileData.getCover());
        setDuration(mp3FileData.getDuration());
        setFileName(mp3FileData.getFileName());
        setGenre(mp3FileData.getGenre());
        setLastModified(mp3FileData.getLastModified());
        setSize(mp3FileData.getSize());
        setTitle(mp3FileData.getTitle());
        setTrack(mp3FileData.getTrack());
        setYear(mp3FileData.getYear());
    }

    public String getFileName() {
        return fileName.get();
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public StringProperty fileNameProperty() {
        return fileName;
    }

    public String getFilePath() {
        return filePath.get();
    }

    public void setFilePath(String filePath) {
        this.filePath.set(filePath);
    }

    public StringProperty filePathProperty() {
        return filePath;
    }

    public String getAbsolutePath() {
        return absolutePath.get();
    }

    public StringProperty absolutePathProperty() {
        return absolutePath;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getArtist() {
        return artist.get();
    }

    public void setArtist(String artist) {
        this.artist.set(artist);
    }

    public StringProperty artistProperty() {
        return artist;
    }

    public String getAlbum() {
        return album.get();
    }

    public void setAlbum(String album) {
        this.album.set(album);
    }

    public StringProperty albumProperty() {
        return album;
    }

    public String getGenre() {
        return genre.get();
    }

    public void setGenre(String genre) {
        this.genre.set(genre);
    }

    public StringProperty genreProperty() {
        return genre;
    }

    public String getTrack() {
        return track.get();
    }

    public void setTrack(String track) {
        this.track.set(track);
    }

    public StringProperty trackProperty() {
        return track;
    }

    public String getYear() {
        return year.get();
    }

    public void setYear(String year) {
        this.year.set(year);
    }

    public StringProperty yearProperty() {
        return year;
    }

    public String getLastModified() {
        return lastModified.get();
    }

    public void setLastModified(String lastModified) {
        this.lastModified.set(lastModified);
    }

    public StringProperty lastModifiedProperty() {
        return lastModified;
    }

    public String getSize() {
        return size.get();
    }

    public void setSize(String size) {
        this.size.set(size);
    }

    public StringProperty sizeProperty() {
        return size;
    }

    public byte[] getCover() {
        return cover.get();
    }

    public void setCover(byte[] cover) {
        this.cover.set(cover);
    }

    public ObjectProperty<byte[]> coverProperty() {
        return cover;
    }

    public double getDuration() {
        return duration.get();
    }

    public void setDuration(double duration) {
        this.duration.set(duration);
    }

    public DoubleProperty durationProperty() {
        return duration;
    }

    public String getFormattedDuration() {
        return formattedDuration.get();
    }

    public StringProperty formattedDurationProperty() {
        return formattedDuration;
    }

    public boolean isCurrentlyPlayed() {
        return currentlyPlayed.get();
    }

    public void setCurrentlyPlayed(boolean playing) {
        this.currentlyPlayed.set(playing);
    }

    public BooleanProperty currentlyPlayedProperty() {
        return currentlyPlayed;
    }

    public boolean isLoaded() {
        return loaded.get();
    }

    public void setLoaded(boolean playing) {
        this.loaded.set(playing);
    }

    public BooleanProperty loadedProperty() {
        return loaded;
    }
}
