package de.pheru.media.core.io.directory;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DefaultDirectorySearcherTest {

    @Test
    public void searchMp3() throws Exception {
        final File file = new File(getClass().getResource("/audiofiles/fake").toURI());
        final String directory = file.getAbsolutePath();
        final List<String> supportedExtensions = Collections.singletonList(".mp3");

        final List<File> files = new DefaultDirectorySearcher().searchFiles(supportedExtensions, directory);
        assertEquals(5, files.size());
        assertTrue(containsFile(files, "1.mp3"));
        assertTrue(containsFile(files, "2.mp3"));
        assertTrue(containsFile(files, "3.mp3"));
        assertTrue(containsFile(files, "4.mp3"));
        assertTrue(containsFile(files, "5.mp3"));
    }

    @Test
    public void searchMp3Wav() throws Exception {
        final File file = new File(getClass().getResource("/audiofiles/fake").toURI());
        final String directory = file.getAbsolutePath();
        final List<String> supportedExtensions = Arrays.asList(".mp3", ".wav");

        final List<File> files = new DefaultDirectorySearcher().searchFiles(supportedExtensions, directory);
        assertEquals(6, files.size());
        assertTrue(containsFile(files, "1.mp3"));
        assertTrue(containsFile(files, "5.mp3"));
        assertTrue(containsFile(files, "1.wav"));
    }

    private boolean containsFile(final List<File> files, final String filename){
        for (final File file : files) {
            if(file.getName().equals(filename)){
                return true;
            }
        }
        return false;
    }

}