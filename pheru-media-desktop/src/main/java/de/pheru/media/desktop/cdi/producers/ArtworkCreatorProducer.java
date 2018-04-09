package de.pheru.media.desktop.cdi.producers;

import de.pheru.media.core.data.loader.ArtworkCreator;
import de.pheru.media.core.data.loader.CachingArtworkCreator;
import de.pheru.media.desktop.cdi.qualifiers.Caching;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class ArtworkCreatorProducer {

    @Produces
    @Caching
    @ApplicationScoped
    public ArtworkCreator artworkCreator() {
        return null; //TODO//new CachingArtworkCreator(cache);
    }
}
