package de.pheru.media.core.mocks;

import de.pheru.media.core.data.loader.ArtworkCreator;
import de.pheru.media.core.data.model.Artwork;

import java.io.IOException;

public class ArtworkCreatorMock implements ArtworkCreator {

    private final Artwork artwork;

    public ArtworkCreatorMock(final Artwork artwork) {
        this.artwork = artwork;
    }

    @Override
    public Artwork createArtwork(final byte[] binaryData) throws IOException {
        return artwork;
    }
}
