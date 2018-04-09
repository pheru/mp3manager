package de.pheru.media.core.io.file;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.pheru.media.core.data.model.Artwork;
import de.pheru.media.core.util.Cache;

import java.io.IOException;

public class CachingArtworkJsonDeserializer extends StdDeserializer<Artwork> {

    private final Cache<Artwork> cache;

    public CachingArtworkJsonDeserializer(final Cache<Artwork> cache) {
        this(null, cache);
    }

    public CachingArtworkJsonDeserializer(final Class<Artwork> t, final Cache<Artwork> cache) {
        super(t);
        this.cache = cache;
    }

    @Override
    public Artwork deserialize(final JsonParser jsonParser,
                               final DeserializationContext deserializationContext)
            throws IOException {
        return cache.get(jsonParser.getIntValue());
    }

}