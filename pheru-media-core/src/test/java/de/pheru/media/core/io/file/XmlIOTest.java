package de.pheru.media.core.io.file;

import de.pheru.media.core.data.model.AudioFile;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.Assert.*;

public class XmlIOTest {

    private static final String WRITE_FILE_NAME = "audiofile_write.xml";
    private static final String WRITE_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><AudioFile><fileName>filenametitle1</fileName><filePath>filepathtitle1</filePath><album>album1</album><artist>artist1</artist><bitrate>100</bitrate><duration>123</duration><genre>TestGenre</genre><size>1000</size><title>title1</title><track>1</track><year>2017</year></AudioFile>";
    private static final String WRITE_DIR_NAME = "writedirname";
    private static final String READ_FILE_NAME = "audiofile_read.xml";
    private static final String INVALID_FILE_NAME_UNKNOWN_TAG = "invalid_audiofile_unknown_tag.xml";
    private static final String INVALID_FILE_NAME_UNPARSABLE_VALUE = "invalid_audiofile_unparsable_value.xml";

    @Test
    public void write() throws Exception {
        final File dir = new File(getClass().getResource("/xml").toURI());
        final File file = new File(dir.getAbsolutePath() + "/" + WRITE_FILE_NAME);

        final AudioFile audioFile = createAudioFile("title1", "album1", "artist1");
        new XmlIO().write(file, AudioFile.class, audioFile);

        assertArrayEquals(WRITE_CONTENT.getBytes(), Files.readAllBytes(file.toPath()));
    }

    @Test
    public void writeNoParentDirectory() throws Exception {
        final File dir = new File(getClass().getResource("/xml").toURI());

        final File newWriteDir = new File(dir.getAbsolutePath() + "/" + WRITE_DIR_NAME);
        if (newWriteDir.exists()) {
            deleteDirectory(newWriteDir.toPath());
        }
        assertFalse(newWriteDir.exists());
        assertFalse(newWriteDir.isDirectory());

        final File file = new File(dir.getAbsolutePath()
                + "/" + WRITE_DIR_NAME + "/" + WRITE_FILE_NAME);

        final AudioFile audioFile = createAudioFile("title1", "album1", "artist1");
        new XmlIO().write(file, AudioFile.class, audioFile);

        assertArrayEquals(WRITE_CONTENT.getBytes(), Files.readAllBytes(file.toPath()));
    }

    @Test
    public void read() throws Exception {
        final File file = new File(getClass().getResource("/xml/" + READ_FILE_NAME).toURI());
        final AudioFile audioFile = new XmlIO().read(file, AudioFile.class);

        assertEquals("title1", audioFile.getTitle());
        assertEquals("album1", audioFile.getAlbum());
        assertEquals("artist1", audioFile.getArtist());
        assertEquals("TestGenre", audioFile.getGenre());
        assertEquals(1, audioFile.getTrack());
        assertEquals("filenametitle1", audioFile.getFileName());
    }

    @Test(expected = IOException.class)
    public void readInvalidFileUnknownTag() throws Exception {
        final File file = new File(getClass().getResource("/xml/" + INVALID_FILE_NAME_UNKNOWN_TAG).toURI());
        new XmlIO().read(file, AudioFile.class);
    }

    @Test(expected = IOException.class)
    public void readInvalidFileUnparsableValue() throws Exception {
        final File file = new File(getClass().getResource("/xml/" + INVALID_FILE_NAME_UNPARSABLE_VALUE).toURI());
        new XmlIO().read(file, AudioFile.class);
    }

    @Test(expected = FileNotFoundException.class)
    public void readFileNotFound() throws Exception {
        final File file = new File("C:/test_gibtshaltnicht.xml");
        new XmlIO().read(file, AudioFile.class);
    }

    private AudioFile createAudioFile(final String title, final String album, final String artist) {
        final AudioFile audioFile = new AudioFile();
        audioFile.setFileName("filename" + title);
        audioFile.setFilePath("filepath" + title);
        audioFile.setTitle(title);
        audioFile.setAlbum(album);
        audioFile.setArtist(artist);
        audioFile.setGenre("TestGenre");
        audioFile.setDuration(123);
        audioFile.setTrack((short) 1);
        audioFile.setYear((short) 2017);
        audioFile.setBitrate((short) 100);
        audioFile.setSize(1000);
        return audioFile;
    }

    private void deleteDirectory(final Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}