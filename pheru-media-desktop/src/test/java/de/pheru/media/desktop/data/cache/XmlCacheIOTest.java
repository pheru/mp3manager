package de.pheru.media.desktop.data.cache;

import de.pheru.media.core.data.model.Artwork;
import de.pheru.media.core.data.model.AudioFile;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class XmlCacheIOTest {

    private static final String WRITE_FILE_NAME = "newcache.xml";
    private static final String READ_FILE_NAME = "cache.xml";
    private static final String INVALID_FILE_NAME_UNKNOWN_TAG = "invalid_cache_unknown_tag.xml";
    private static final String INVALID_FILE_NAME_UNPARSABLE_VALUE = "invalid_cache_unparsable_value.xml";

    @Test
    public void writeCacheFile() throws Exception {
        final File cacheDir = new File(getClass().getResource("/cache").toURI());
        final File file = new File(cacheDir.getAbsolutePath() + "/" + WRITE_FILE_NAME);

        // Artist 1 - Album 1
        final AudioFile artist1album1title1 = createAudioFile("title1", "album1", "artist1");
        final AudioFile artist1album1title2 = createAudioFile("title2", "album1", "artist1");
        final AudioFile artist1album1title3 = createAudioFile("title3", "album1", "artist1");
        final Artwork artist1album1artwork1 = createArtwork(11, 1);
        final Artwork artist1album1artwork2 = createArtwork(11, 2);

        final AlbumCache artist1album1 = new AlbumCache();
        artist1album1.setArtworks(new ArrayList<>());
        artist1album1.getArtworks().add(artist1album1artwork1);
        artist1album1.getArtworks().add(artist1album1artwork2);
        artist1album1.setAudioFiles(new ArrayList<>());
        artist1album1.getAudioFiles().add(artist1album1title1);
        artist1album1.getAudioFiles().add(artist1album1title2);
        artist1album1.getAudioFiles().add(artist1album1title3);

        // Artist 1 - Album 2
        final AudioFile artist1album2title1 = createAudioFile("title1", "album2", "artist1");
        final AudioFile artist1album2title2 = createAudioFile("title2", "album2", "artist1");
        final AudioFile artist1album2title3 = createAudioFile("title3", "album2", "artist1");
        final Artwork artist1album2artwork1 = createArtwork(12, 1);
        final Artwork artist1album2artwork2 = createArtwork(12, 2);

        final AlbumCache artist1album2 = new AlbumCache();
        artist1album2.setArtworks(new ArrayList<>());
        artist1album2.getArtworks().add(artist1album2artwork1);
        artist1album2.getArtworks().add(artist1album2artwork2);
        artist1album2.setAudioFiles(new ArrayList<>());
        artist1album2.getAudioFiles().add(artist1album2title1);
        artist1album2.getAudioFiles().add(artist1album2title2);
        artist1album2.getAudioFiles().add(artist1album2title3);

        // Artist 1
        final ArtistCache artist1 = new ArtistCache();
        artist1.setAlbumCaches(new HashMap<>());
        artist1.getAlbumCaches().put("album1", artist1album1);
        artist1.getAlbumCaches().put("album2", artist1album2);

        // Artist 2 - Album 1
        final AudioFile artist2album1title1 = createAudioFile("title1", "album1", "artist2");
        final Artwork artist2album1artwork1 = createArtwork(21, 1);

        final AlbumCache artist2album1 = new AlbumCache();
        artist2album1.setArtworks(new ArrayList<>());
        artist2album1.getArtworks().add(artist2album1artwork1);
        artist2album1.setAudioFiles(new ArrayList<>());
        artist2album1.getAudioFiles().add(artist2album1title1);

        // Artist 2
        final ArtistCache artist2 = new ArtistCache();
        artist2.setAlbumCaches(new HashMap<>());
        artist2.getAlbumCaches().put("album1", artist2album1);

        final Cache cache = new Cache();
        cache.setArtistCaches(new HashMap<>());
        cache.getArtistCaches().put("artist1", artist1);
        cache.getArtistCaches().put("artist2", artist2);

        new XmlCacheIO().writeCacheFile(file, cache);
    }

    @Test
    public void readCacheFile() throws Exception {
        final File file = new File(getClass().getResource("/cache/" + READ_FILE_NAME).toURI());
        final Cache cache = new XmlCacheIO().readCacheFile(file);

        // 2 Artists
        assertEquals(2, cache.getArtistCaches().size());

        // Artist1 mit 2 Alben, Artist2 mit 1 Album
        assertEquals(2, cache.getArtistCache("artist1").getAlbumCaches().size());
        assertEquals(1, cache.getArtistCache("artist2").getAlbumCaches().size());

        final AlbumCache artist1album1 = cache.getArtistCache("artist1").getAlbumCache("album1");
        assertEquals(3, artist1album1.getAudioFiles().size());
        assertEquals(2, artist1album1.getArtworks().size());

        final AlbumCache artist2album1 = cache.getArtistCache("artist2").getAlbumCache("album1");
        final List<AudioFile> artist2album1AudioFiles = artist2album1.getAudioFiles();
        assertEquals(1, artist2album1AudioFiles.size());
        assertEquals("title1", artist2album1AudioFiles.get(0).getTitle());
        assertEquals("album1", artist2album1AudioFiles.get(0).getAlbum());
        assertEquals("artist2", artist2album1AudioFiles.get(0).getArtist());
        assertEquals("TestGenre", artist2album1AudioFiles.get(0).getGenre());
        assertEquals(1, artist2album1AudioFiles.get(0).getTrack());
        assertEquals("filenametitle1", artist2album1AudioFiles.get(0).getFileName());


        final List<Artwork> artist2album1Artworks = artist2album1.getArtworks();
        assertEquals(1, artist2album1Artworks.size());
        assertEquals(21, artist2album1Artworks.get(0).getWidth());
        assertEquals(1, artist2album1Artworks.get(0).getHeight());
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}, artist2album1Artworks.get(0).getBinaryData());
    }

    @Test(expected = IOException.class)
    public void readInvalidCacheFileUnknownTag() throws Exception {
        final File file = new File(getClass().getResource("/cache/" + INVALID_FILE_NAME_UNKNOWN_TAG).toURI());
        new XmlCacheIO().readCacheFile(file);
    }

    @Test(expected = IOException.class)
    public void readInvalidCacheFileUnparsableValue() throws Exception {
        final File file = new File(getClass().getResource("/cache/" + INVALID_FILE_NAME_UNPARSABLE_VALUE).toURI());
        new XmlCacheIO().readCacheFile(file);
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

    private Artwork createArtwork(final int width, final int height) {
        final byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        return new Artwork(bytes, width, height);
    }

}