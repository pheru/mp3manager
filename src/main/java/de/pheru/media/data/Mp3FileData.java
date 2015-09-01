package de.pheru.media.data;

import de.pheru.media.exceptions.Mp3FileDataException;
import de.pheru.media.exceptions.RenameFailedException;
import de.pheru.media.exceptions.SaveFailedException;
import de.pheru.media.gui.applicationwindow.editfile.EditFilePresenter;
import de.pheru.media.util.ByteUtil;
import de.pheru.media.util.TimeUtil;
import java.io.File;
import java.io.IOException;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.reference.PictureTypes;

/**
 * Datenmodell für die Mp3-Dateien.
 *
 * @author Philipp Bruckner
 */
public class Mp3FileData extends FileBasedData {

    public static final Mp3FileData PLACEHOLDER_DATA = createPlaceholderData();
    public static final Mp3FileData EMPTY_DATA = createEmptyData();
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
                return TimeUtil.secondsToDurationFormat(duration.get(), false);
            }
        });
    }

    /**
     * Erstellt ein Mp3FileData-Objekt.
     *
     * @param file Die MP3-Datei.
     * @throws de.pheru.media.exceptions.Mp3FileDataException Wenn ein Fehler
     * beim Lesen der MP3-Informationen auftritt.
     */
    public Mp3FileData(File file) throws Mp3FileDataException {
        this();
        fileName.set(file.getName());
        filePath.set(file.getParent());
        size.set(ByteUtil.bytesToMB(file.length()));
        lastModified.set(TimeUtil.millisecondsToDateFormat(file.lastModified()));

        MP3File mp3File;
        try {
            mp3File = (MP3File) AudioFileIO.read(file);
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
            throw new Mp3FileDataException("Failed to read MP3-Data from file \"" + file.getAbsolutePath() + "\"!", e);
        }
        AudioHeader audioHeader = mp3File.getAudioHeader();

        duration.set(audioHeader.getTrackLength());
        bitrate.set(audioHeader.getBitRate().replace("~", "") + Mp3FileData.UNIT_BITRATE);
        if (mp3File.hasID3v2Tag()) {
            AbstractID3v2Tag tag = mp3File.getID3v2Tag();
            title.set(tag.getFirst(FieldKey.TITLE));
            artist.set(tag.getFirst(FieldKey.ARTIST));
            album.set(tag.getFirst(FieldKey.ALBUM));
            genre.set(tag.getFirst(FieldKey.GENRE));
            year.set(tag.getFirst(FieldKey.YEAR));
            track.set(tag.getFirst(FieldKey.TRACK));
            if (tag.getFirstArtwork() != null) {
                Artwork artwork = tag.getFirstArtwork();
                artworkData.set(new ArtworkData(artwork.getBinaryData(), artwork.getMimeType()));
            }
        } else {
            throw new Mp3FileDataException("File \"" + file.getAbsolutePath() + "\" does not have an ID3v2Tag!");
        }
    }

    /**
     * Kopier-Konstruktor
     *
     * @param copyData Kopierdaten.
     */
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

    /**
     * Speichert die geänderten MP3-Informationen ab.
     *
     * @param changeData Die zu speichernden MP3-Informationen.
     * @throws de.pheru.media.exceptions.RenameFailedException Wenn das
     * Umbennen der Datei fehlschlägt.
     * @throws de.pheru.media.exceptions.SaveFailedException Wenn das
     * Speichern fehlschlägt.
     */
    public void save(Mp3FileData changeData) throws RenameFailedException, SaveFailedException {
        File file = new File(absolutePath.get());
        if (!changeData.getFileName().equals(EditFilePresenter.NOT_EDITABLE + ".mp3")
                && !fileName.get().equals(changeData.getFileName())) {
            File newFile = new File(filePath.get() + "\\" + changeData.getFileName());
            if (file.renameTo(newFile)) {
                file = newFile;
                fileName.set(changeData.getFileName());
            } else {
                throw new RenameFailedException("Could not rename file " + fileName.get() + " to " + changeData.getFileName());
            }
        }

        try {
            MP3File mp3File = (MP3File) AudioFileIO.read(file);
            AbstractID3v2Tag tag = mp3File.getID3v2Tag();
            setTagField(tag, FieldKey.TITLE, changeData.getTitle());
            setTagField(tag, FieldKey.ARTIST, changeData.getArtist());
            setTagField(tag, FieldKey.ALBUM, changeData.getAlbum());
            setTagField(tag, FieldKey.GENRE, changeData.getGenre());
            setTagField(tag, FieldKey.YEAR, changeData.getYear());
            setTagField(tag, FieldKey.TRACK, changeData.getTrack());

            if (changeData.getArtworkData() != null && changeData.getArtworkData().getBinaryData().length > 0) {
                Artwork newArtwork = new Artwork();
                newArtwork.setBinaryData(changeData.getArtworkData().getBinaryData());
                newArtwork.setMimeType(changeData.getArtworkData().getMimeType());
                newArtwork.setDescription("");
                newArtwork.setPictureType(PictureTypes.DEFAULT_ID); //DEFAULT_ID == Cover (Front)
                tag.deleteArtworkField();
                tag.setField(newArtwork);
            }
            mp3File.save();
            //TODO reload
        } catch (CannotReadException | IOException | ReadOnlyFileException | TagException | InvalidAudioFrameException e) {
            throw new SaveFailedException("Failed to save\n"
                    + ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE, true)
                    + "\nwith changeData: " + ToStringBuilder.reflectionToString(changeData, ToStringStyle.MULTI_LINE_STYLE, true), e);
        }
    }

    private void setTagField(AbstractID3v2Tag tag, FieldKey key, String value) throws KeyNotFoundException, FieldDataInvalidException {
        if (!value.equals(EditFilePresenter.DIFF_VALUES)) {
            tag.setField(key, value);
        }
    }

    private static Mp3FileData createEmptyData() {
        Mp3FileData empty = new Mp3FileData();
        empty.formattedDuration.unbind();
        empty.formattedDuration.set("");
        return empty;
    }

    private static Mp3FileData createPlaceholderData() {
        Mp3FileData placeholderData = new Mp3FileData();
        placeholderData.setTitle("<Titel>");
        placeholderData.setAlbum("<Album>");
        placeholderData.setArtist("<Interpret>");
        placeholderData.setDuration(0.0);
        return placeholderData;
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
