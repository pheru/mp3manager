package de.pheru.media.core.io.file;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import de.pheru.media.core.data.model.Artwork;
import de.pheru.media.core.util.Cache;

import java.io.IOException;

public class CachingArtworkJsonSerializer extends StdSerializer<Artwork> {

    private final Cache<Artwork> cache;

    public CachingArtworkJsonSerializer(final Cache<Artwork> cache) {
        this(null, cache);
    }

    public CachingArtworkJsonSerializer(final Class<Artwork> t, final Cache<Artwork> cache) {
        super(t);
        this.cache = cache;
    }

    @Override
    public void serialize(final Artwork value,
                          final JsonGenerator jsonGenerator,
                          final SerializerProvider provider)
            throws IOException {
        jsonGenerator.writeNumber(cache.getKey(value));
    }
}