package de.pheru.media.data;

import de.pheru.media.exceptions.Mp3FileDataException;
import de.pheru.media.exceptions.RenameFailedException;
import de.pheru.media.exceptions.SaveFailedException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;

import java.io.File;
import java.io.IOException;

/**
 * Datenmodell für die Mp3-Dateien.
 */
public class Mp3FileData extends FileBasedData {

    private static final Logger LOGGER = LogManager.getLogger(Mp3FileData.class);

    public static final Mp3FileData PLACEHOLDER_DATA = createPlaceholderData();
    public static final Mp3FileData EMPTY_DATA = new Mp3FileData();
    public static final String UNIT_SIZE = " MB";
    public static final String UNIT_BITRATE = " kBit/s";
    public static final String FIELD_DIFF_VALUES = "<Verschiedene Werte>";
    public static final String FIELD_NOT_EDITABLE = "<Bei Mehrfachauswahl nicht editierbar>";

    private String title;
    private String album;
    private String artist;
    private String genre;
    private short track;
    private short year;
    private long size;
    private long lastModified;
    private short bitrate;
    private int duration;

    private static Mp3FileData createPlaceholderData() {
        Mp3FileData placeholderData = new Mp3FileData();
        placeholderData.title = "<Titel>";
        placeholderData.album = "<Album>";
        placeholderData.artist = "<Interpret>";
        placeholderData.duration = 0;
        return placeholderData;
    }

    /**
     * Erstellt ein leeres Mp3FileData-Objekt.
     */
    public Mp3FileData() {
        super("", "");
        title = "";
        album = "";
        artist = "";
        genre = "";
        track = 0;
        year = 0;
        size = 0;
        lastModified = 0;
        bitrate = 0;
        duration = 0;
    }

    /**
     * Erstellt ein Mp3FileData-Objekt.
     *
     * @param file Die MP3-Datei.
     * @throws de.pheru.media.exceptions.Mp3FileDataException Wenn ein Fehler
     *                                                        beim Lesen der MP3-Informationen auftritt.
     */
    public Mp3FileData(final File file) throws Mp3FileDataException {
        loadDataFromFile(file);
    }

    private void loadDataFromFile(final File file) throws Mp3FileDataException {
        fileName = file.getName();
        filePath = file.getParent();
        size = file.length();
        lastModified = file.lastModified();

        final MP3File mp3File;
        try {
            mp3File = (MP3File) AudioFileIO.read(file);
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
            throw new Mp3FileDataException("Failed to read MP3-Data from file \"" + file.getAbsolutePath() + "\"!", e);
        }
        final AudioHeader audioHeader = mp3File.getAudioHeader();

        duration = audioHeader.getTrackLength();
        bitrate = getShortFromTagValue(audioHeader.getBitRate());
        if (mp3File.hasID3v2Tag()) {
            final AbstractID3v2Tag tag = mp3File.getID3v2Tag();
            title = tag.getFirst(FieldKey.TITLE);
            artist = tag.getFirst(FieldKey.ARTIST);
            album = tag.getFirst(FieldKey.ALBUM);
            genre = tag.getFirst(FieldKey.GENRE);
            year = getShortFromTagValue(tag.getFirst(FieldKey.YEAR));
            track = getShortFromTagValue(tag.getFirst(FieldKey.TRACK));
            //TODO
//            if (tag.getFirstArtwork() != null) {
//                Artwork artwork = tag.getFirstArtwork();
//                try {
//                    BufferedImage image = (BufferedImage) artwork.getImage();
//                    artworkData.set(new ArtworkData(artwork.getBinaryData(), image.getWidth(),
//                            image.getHeight(), artwork.getMimeType()));
//                } catch (IOException e) {
//                    LOGGER.warn("Exception retrieving image from mp3file!", e);
//                    artworkData.set(new ArtworkData(artwork.getBinaryData(), 0, 0, artwork.getMimeType()));
//                }
//            }
        } else {
            throw new Mp3FileDataException("File \"" + file.getAbsolutePath() + "\" does not have an ID3v2Tag!");
        }
    }

    private short getShortFromTagValue(final String tagValue) {
        if (tagValue == null) {
            return -1;
        } else {
            final String replace = tagValue.replaceAll("[\\D]", "");
            if (replace.isEmpty()) {
                return -1;
            } else {
                return Short.valueOf(replace);
            }
        }
    }

    private int getIntFromTagValue(final String tagValue) {
        if (tagValue == null) {
            return -1;
        } else {
            final String replace = tagValue.replaceAll("[\\D]", "");
            if (replace.isEmpty()) {
                return -1;
            } else {
                return Integer.valueOf(replace);
            }
        }
    }

    private long getLongFromTagValue(final String tagValue) {
        if (tagValue == null) {
            return -1;
        } else {
            final String replace = tagValue.replaceAll("[\\D]", "");
            if (replace.isEmpty()) {
                return -1;
            } else {
                return Long.valueOf(replace);
            }
        }
    }

    /**
     * Speichert die geänderten MP3-Informationen ab.
     *
     * @param changeData Die zu speichernden MP3-Informationen.
     * @throws de.pheru.media.exceptions.RenameFailedException Wenn das Umbennen der Datei fehlschlägt.
     * @throws de.pheru.media.exceptions.SaveFailedException   Wenn das Speichern fehlschlägt.
     * @throws de.pheru.media.exceptions.Mp3FileDataException  Wenn das erneute Laden der Mp3FileData fehlschlägt.
     */
    public void save(final Mp3FileData changeData) throws RenameFailedException, SaveFailedException, Mp3FileDataException {
        File file = new File(getAbsolutePath());
        if (!changeData.getFileName().equals(FIELD_NOT_EDITABLE + ".mp3")
                && !fileName.equals(changeData.getFileName())) {
            file = renameFile(file, changeData.getFileName());
        }
        try {
            MP3File mp3File = (MP3File) AudioFileIO.read(file);
            AbstractID3v2Tag tag = mp3File.getID3v2Tag();
            setTagFields(tag, changeData);

            //TODO
//            if (changeData.getArtworkData() != null && changeData.getArtworkData().getBinaryData().length > 0) {
//                setArtworkTagField(tag, changeData);
//            }
            mp3File.save();
        } catch (CannotReadException | IOException | ReadOnlyFileException | TagException | InvalidAudioFrameException e) {
            throw new SaveFailedException("Failed to save\n"
                    + ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE, true)
                    + "\nwith changeData: " + ToStringBuilder.reflectionToString(changeData, ToStringStyle.MULTI_LINE_STYLE, true), e);
        }
        loadDataFromFile(file);
    }

    private void setTagFields(AbstractID3v2Tag tag, Mp3FileData changeData) throws FieldDataInvalidException {
        setTagField(tag, FieldKey.TITLE, changeData.getTitle());
        setTagField(tag, FieldKey.ARTIST, changeData.getArtist());
        setTagField(tag, FieldKey.ALBUM, changeData.getAlbum());
        setTagField(tag, FieldKey.GENRE, changeData.getGenre());
        setTagField(tag, FieldKey.YEAR, String.valueOf(changeData.getYear()));
        setTagField(tag, FieldKey.TRACK, String.valueOf(changeData.getTrack()));
    }

    //TODO
//    private void setArtworkTagField(AbstractID3v2Tag tag, Mp3FileData changeData) throws FieldDataInvalidException {
//        Artwork newArtwork = new StandardArtwork();
//        newArtwork.setBinaryData(changeData.getArtworkData().getBinaryData());
//        newArtwork.setMimeType(changeData.getArtworkData().getMimeType());
//        newArtwork.setDescription("");
//        newArtwork.setPictureType(PictureTypes.DEFAULT_ID); //DEFAULT_ID == Cover (Front)
//        tag.deleteArtworkField();
//        tag.setField(newArtwork);
//    }

    private File renameFile(final File file, final String newFileName) throws RenameFailedException {
        File newFile = new File(filePath + "\\" + newFileName);
        if (file.renameTo(newFile)) {
            fileName = newFileName;
            return newFile;
        } else {
            throw new RenameFailedException("Could not rename file " + fileName + " to " + newFileName);
        }
    }

    private void setTagField(AbstractID3v2Tag tag, FieldKey key, String value) throws FieldDataInvalidException {
        if (!value.equals(FIELD_DIFF_VALUES)) {
            tag.setField(key, value);
        }
    }

    @Override
    public String toString() {
        return title + " - " + album + " - " + artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public short getTrack() {
        return track;
    }

    public void setTrack(short track) {
        this.track = track;
    }

    public short getYear() {
        return year;
    }

    public void setYear(short year) {
        this.year = year;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public short getBitrate() {
        return bitrate;
    }

    public void setBitrate(short bitrate) {
        this.bitrate = bitrate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
