package de.pheru.media.core.data.loader;

import de.pheru.media.core.data.loader.AudioFileLoader;
import de.pheru.media.core.data.loader.DefaultAudioFileLoaderProvider;
import de.pheru.media.core.data.loader.Mp3FileLoader;
import de.pheru.media.core.data.loader.UnsupportedAudioFileFormatException;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DefaultAudioFileLoaderProviderTest {

    @Test
    public void getSupportedFileExtensions() {
        final List<String> supportedFileExtensions = new DefaultAudioFileLoaderProvider().getSupportedFileExtensions();
        assertEquals(1, supportedFileExtensions.size());
        assertTrue(supportedFileExtensions.contains(".mp3"));
    }

    @Test
    public void getLoaderForMp3File() throws Exception {
        final File mp3File = new File("test.mp3");
        final AudioFileLoader loader = new DefaultAudioFileLoaderProvider().getLoaderForFile(mp3File);
        assertTrue(loader.getClass().equals(Mp3FileLoader.class));
    }

    @Test(expected = UnsupportedAudioFileFormatException.class)
    public void getLoaderForUnsupportedFile() throws Exception {
        final File wavFile = new File("test.wav");
        new DefaultAudioFileLoaderProvider().getLoaderForFile(wavFile);
    }

}