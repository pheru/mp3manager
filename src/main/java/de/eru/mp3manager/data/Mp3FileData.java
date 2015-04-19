package de.eru.mp3manager.data;

import de.eru.mp3manager.data.utils.Mp3Mapper;
import de.eru.mp3manager.utils.formatter.ByteFormatter;
import de.eru.mp3manager.utils.formatter.TimeFormatter;
import java.io.File;
import java.io.IOException;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

/**
 * Datenmodell für die Mp3-Dateien.
 *
 * @author Philipp Bruckner
 */
public class Mp3FileData extends FileBasedData {

    public static final Mp3FileData MUSICPLAYER_PLACEHOLDER_DATA = new Mp3FileData("<Titel>", "<Album>", "<Interpret>", 0.0);
    public static final Mp3FileData EMPTY_PLAYLIST_DATA = createEmptyData();
    public static final String UNIT_SIZE = " MB";
    public static final String UNIT_BITRATE = " kBit/s";

    private final StringProperty title = new SimpleStringProperty("");
    private final StringProperty album = new SimpleStringProperty("");
    private final StringProperty artist = new SimpleStringProperty("");
    private final StringProperty genre = new SimpleStringProperty("");
    private final StringProperty track = new SimpleStringProperty("");
    private final StringProperty year = new SimpleStringProperty("");
    private final StringProperty size = new SimpleStringProperty("");
    private final StringProperty lastModified = new SimpleStringProperty("");
    private final StringProperty bitrate = new SimpleStringProperty("");
    private final ObjectProperty<ArtworkData> artworkData = new SimpleObjectProperty<>();
    private final DoubleProperty duration = new SimpleDoubleProperty(0);
    private final StringProperty formattedDuration = new SimpleStringProperty("");

    /**
     * Erstellt ein leeres Mp3FileData-Objekt.
     */
    public Mp3FileData() {
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
        lastModified.set(TimeFormatter.millisecondsToDateFormat(file.lastModified()));
    }

    public Mp3FileData(Mp3FileData copyData) {
        this();
        fileName.set(copyData.getFileName());
        filePath.set(copyData.getFilePath());
        title.set(copyData.getTitle());
        album.set(copyData.getAlbum());
        artist.set(copyData.getArtist());
        genre.set(copyData.getGenre());
        track.set(copyData.getTrack());
        year.set(copyData.getYear());
        size.set(copyData.getSize());
        lastModified.set(copyData.getLastModified());
        artworkData.set(copyData.getArtworkData());
        duration.set(copyData.getDuration());
    }

    private Mp3FileData(String title, String album, String artist, double duration) {
        this();
        this.title.set(title);
        this.album.set(album);
        this.artist.set(artist);
        this.duration.set(duration);
    }

    public void reload() {
        try {
            Mp3Mapper.fileToMp3FileData(new File(absolutePath.get()), this);
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException ex) {
            ex.printStackTrace();
        }
    }

    private static Mp3FileData createEmptyData() {
        Mp3FileData empty = new Mp3FileData("", "", "", 0.0);
        empty.formattedDuration.unbind();
        empty.formattedDuration.set("");
        return empty;
    }

    @Override
    public String toString() {
        return "[" + title.get() + "] - [" + album.get() + "] - [" + artist.get() + "]";
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

    public String getBitrate() {
        return bitrate.get();
    }

    public void setBitrate(final String bitrate) {
        this.bitrate.set(bitrate);
    }

    public StringProperty bitrateProperty() {
        return bitrate;
    }

    public ArtworkData getArtworkData() {
        return artworkData.get();
    }

    public void setArtworkData(final ArtworkData artworkData) {
        this.artworkData.set(artworkData);
    }

    public ObjectProperty<ArtworkData> artworkDataProperty() {
        return artworkData;
    }
}
