package de.pheru.media.core.data.loader;

import de.pheru.media.core.data.model.Artwork;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CachingArtworkCreatorTest {

    @Test
    public void createArtwork() throws Exception {
        final InputStream resourceAsStream = getClass().getResourceAsStream("/images/kamelot_epica.jpg");
        final byte[] binaryData = new byte[resourceAsStream.available()];
        resourceAsStream.read(binaryData);

        //TODO
//        final CachingArtworkCreator artworkCreator = new CachingArtworkCreator(cache);
//        final Artwork artwork = artworkCreator.createArtwork(binaryData);
//
//        assertEquals(29633, artwork.getBinaryData().length);
//        assertEquals(300, artwork.getWidth());
//        assertEquals(300, artwork.getHeight());
//
//        final Artwork artwork2 = artworkCreator.createArtwork(binaryData);
//
//        assertEquals(artwork, artwork2);
//        assertTrue(artwork == artwork2);
    }

}