package de.pheru.media.core.data.loader;

import de.pheru.media.core.data.model.Artwork;

import java.io.IOException;

public interface ArtworkCreator {

    Artwork createArtwork(final byte[] binaryData) throws IOException;
}
