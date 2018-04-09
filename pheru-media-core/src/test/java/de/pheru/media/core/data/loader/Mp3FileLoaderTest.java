package de.pheru.media.core.data.loader;

import de.pheru.media.core.data.model.Artwork;
import de.pheru.media.core.data.model.AudioFile;
import de.pheru.media.core.mocks.ArtworkCreatorMock;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Mp3FileLoaderTest {

    private static final String FILE_NAME = "01-01-Agnus Dei (Intro).mp3";

    @Test
    public void getSupportedExtensions() {
        final List<String> supportedExtensions = new Mp3FileLoader().getSupportedExtensions();
        assertTrue(supportedExtensions.contains(".mp3"));
    }

    @Test
    public void load() throws Exception {
        final File file = new File(getClass().getResource("/audiofiles/real/" + FILE_NAME).toURI());
        final Artwork mockArtwork = new Artwork(new byte[0], 50, 100);
        final AudioFile audioFile = new Mp3FileLoader().load(file, new ArtworkCreatorMock(mockArtwork));

        assertEquals(FILE_NAME, audioFile.getFileName());
        assertTrue(audioFile.getFilePath().endsWith("test-classes\\audiofiles\\real"));

        assertEquals("Agnus Dei (Intro)", audioFile.getTitle());
        assertEquals("Blood of the Saints", audioFile.getAlbum());
        assertEquals("Powerwolf", audioFile.getArtist());
        assertEquals("Hard Rock & Metal", audioFile.getGenre());

        assertEquals(48, audioFile.getDuration());
        assertEquals(1, audioFile.getTrack());
        assertEquals(2011, audioFile.getYear());
        assertEquals(251, audioFile.getBitrate());

        assertEquals(1603261, audioFile.getSize());

        assertArrayEquals(mockArtwork.getBinaryData(), audioFile.getArtwork().getBinaryData());
        assertEquals(mockArtwork.getWidth(), audioFile.getArtwork().getWidth());
        assertEquals(mockArtwork.getHeight(), audioFile.getArtwork().getHeight());
    }

}