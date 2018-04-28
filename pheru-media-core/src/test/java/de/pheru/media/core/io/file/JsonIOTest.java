package de.pheru.media.core.io.file;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.node.IntNode;
import de.pheru.media.core.data.model.Artwork;
import de.pheru.media.core.data.model.AudioFile;
import de.pheru.media.core.util.Cache;
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

public class JsonIOTest {

    private static final String BASE_DIR = "/json";
    private static final String WRITE_DIR = "writedir";

    private static final String READ_FILE_NAME = "audiofile_read.json";

    private static final String WRITE_FILE_NAME_AUDIOFILE = "audiofile_write.json";
    private static final String WRITE_CONTENT_AUDIOFILE = "{\"fileName\":\"filenametitle1\",\"filePath\":\"filepathtitle1\",\"title\":\"title1\",\"album\":\"album1\",\"artist\":\"artist1\",\"genre\":\"TestGenre\",\"duration\":123,\"track\":1,\"year\":2017,\"bitrate\":100,\"size\":1000,\"artwork\":{\"binaryData\":\"AAECAwQFBgcI\",\"width\":50,\"height\":50}}";
    private static final String WRITE_CONTENT_AUDIOFILE_SERIALIZER = "{\"fileName\":\"filenametitle1\",\"filePath\":\"filepathtitle1\",\"title\":\"title1\",\"album\":\"album1\",\"artist\":\"artist1\",\"genre\":\"TestGenre\",\"duration\":123,\"track\":1,\"year\":2017,\"bitrate\":100,\"size\":1000,\"artwork\":246810}";

    private static final String WRITE_FILE_NAME_CACHE = "cache_write.json";
    private static final String WRITE_CONTENT_CACHE = "{\"cache\":{\"1\":\"Eins\",\"2\":\"Zwei\"},\"currentKey\":2}";

    private static final String INVALID_FILE_NAME_UNKNOWN_KEY = "invalid_audiofile_unknown_key.json";
    private static final String INVALID_FILE_NAME_UNPARSABLE_VALUE = "invalid_audiofile_unparsable_value.json";

    @Test
    public void writeAudioFile() throws Exception {
        final File dir = new File(getClass().getResource(BASE_DIR).toURI());
        final File file = new File(dir.getAbsolutePath() + "/" + WRITE_FILE_NAME_AUDIOFILE);

        final AudioFile audioFile = createAudioFile("title1", "album1", "artist1");
        new JsonIO().write(file, AudioFile.class, audioFile);

        assertArrayEquals(WRITE_CONTENT_AUDIOFILE.getBytes(), Files.readAllBytes(file.toPath()));
    }

    @Test
    public void writeAudioFileWithSerializer() throws Exception {
        final File dir = new File(getClass().getResource(BASE_DIR).toURI());
        final File file = new File(dir.getAbsolutePath() + "/" + WRITE_FILE_NAME_AUDIOFILE);

        final AudioFile audioFile = createAudioFile("title1", "album1", "artist1");
        final JsonIO jsonIO = new JsonIO();
        jsonIO.addJsonSerializer(Artwork.class, new JsonSerializer<Artwork>() {
            @Override
            public void serialize(final Artwork artwork, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeNumber(246810);
            }
        });
        jsonIO.write(file, AudioFile.class, audioFile);

        assertArrayEquals(WRITE_CONTENT_AUDIOFILE_SERIALIZER.getBytes(), Files.readAllBytes(file.toPath()));
    }

    @Test
    public void writeCache() throws Exception {
        final File dir = new File(getClass().getResource(BASE_DIR).toURI());
        final File file = new File(dir.getAbsolutePath() + "/" + WRITE_FILE_NAME_CACHE);

        final Cache<String> cache = new Cache<>();
        cache.add("Eins");
        cache.add("Zwei");
        new JsonIO().write(file, Cache.class, cache);

        assertArrayEquals(WRITE_CONTENT_CACHE.getBytes(), Files.readAllBytes(file.toPath()));
    }

    @Test
    public void writeNoParentDirectory() throws Exception {
        final File dir = new File(getClass().getResource(BASE_DIR).toURI());

        final File newWriteDir = new File(dir.getAbsolutePath() + "/" + WRITE_DIR);
        if (newWriteDir.exists()) {
            deleteDirectory(newWriteDir.toPath());
        }
        assertFalse(newWriteDir.exists());
        assertFalse(newWriteDir.isDirectory());

        final File file = new File(dir.getAbsolutePath()
                + "/" + WRITE_DIR + "/" + WRITE_FILE_NAME_AUDIOFILE);

        final AudioFile audioFile = createAudioFile("title1", "album1", "artist1");
        new JsonIO().write(file, AudioFile.class, audioFile);

        assertArrayEquals(WRITE_CONTENT_AUDIOFILE.getBytes(), Files.readAllBytes(file.toPath()));
    }

    @Test
    public void read() throws Exception {
        final File file = new File(getClass().getResource(BASE_DIR + "/" + READ_FILE_NAME).toURI());
        final AudioFile audioFile = new JsonIO().read(file, AudioFile.class);

        assertEquals("title1", audioFile.getTitle());
        assertEquals("album1", audioFile.getAlbum());
        assertEquals("artist1", audioFile.getArtist());
        assertEquals("TestGenre", audioFile.getGenre());
        assertEquals(1, audioFile.getTrack());
        assertEquals("filenametitle1", audioFile.getFileName());
    }

    @Test
    public void readWithDeserializer() throws Exception {
        final File file = new File(getClass().getResource(BASE_DIR + "/" + READ_FILE_NAME).toURI());
        final JsonIO jsonIO = new JsonIO();
        jsonIO.addJsonDeserializer(Artwork.class, new JsonDeserializer<Artwork>() {
            @Override
            public Artwork deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
                // readTree nötig, damit keine UnrecognizedPropertyException geworfen wird
                jsonParser.getCodec().readTree(jsonParser);
                return new Artwork(new byte[99], 555, 909);
            }
        });
        final AudioFile audioFile = jsonIO.read(file, AudioFile.class);

        assertEquals("title1", audioFile.getTitle());
        assertEquals("album1", audioFile.getAlbum());
        assertEquals("artist1", audioFile.getArtist());
        assertEquals("TestGenre", audioFile.getGenre());
        assertEquals(1, audioFile.getTrack());
        assertEquals("filenametitle1", audioFile.getFileName());
        assertEquals(99, audioFile.getArtwork().getBinaryData().length);
        assertEquals(555, audioFile.getArtwork().getWidth());
        assertEquals(909, audioFile.getArtwork().getHeight());
    }

    @Test(expected = FileNotFoundException.class)
    public void readFileNotFound() throws Exception {
        final File file = new File("C:/test_gibtshaltnicht.json");
        new JsonIO().read(file, AudioFile.class);
    }

    @Test(expected = UnrecognizedPropertyException.class)
    public void readInvalidFileUnknownKey() throws Exception {
        final File file = new File(getClass().getResource(BASE_DIR + "/" + INVALID_FILE_NAME_UNKNOWN_KEY).toURI());
        new JsonIO().read(file, AudioFile.class);
    }

    @Test(expected = InvalidFormatException.class)
    public void readInvalidFileUnparsableValue() throws Exception {
        final File file = new File(getClass().getResource(BASE_DIR + "/" + INVALID_FILE_NAME_UNPARSABLE_VALUE).toURI());
        new JsonIO().read(file, AudioFile.class);
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
        audioFile.setArtwork(new Artwork(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8}, 50, 50));
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