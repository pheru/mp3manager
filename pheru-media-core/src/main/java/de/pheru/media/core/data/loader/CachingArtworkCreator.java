package de.pheru.media.core.data.loader;

import de.pheru.media.core.data.model.Artwork;
import de.pheru.media.core.util.Cache;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CachingArtworkCreator implements ArtworkCreator {

    private final Cache<Artwork> cache;

    public CachingArtworkCreator(final Cache<Artwork> cache) {
        this.cache = cache;
    }

    @Override
    public Artwork createArtwork(final byte[] binaryData) throws IOException {
        final ImageInputStream inputStream = ImageIO.createImageInputStream(new ByteArrayInputStream(binaryData));
        final BufferedImage image = ImageIO.read(inputStream);


        final List<Artwork> cachedArtwork = cache.get(cacheItem -> Arrays.equals(cacheItem.getBinaryData(), binaryData));
        if (!cachedArtwork.isEmpty()) {
            return cachedArtwork.get(0);
        }
        final Artwork artwork = new Artwork(binaryData, image.getWidth(), image.getHeight());
        cache.add(artwork);
        return artwork;
    }
}
