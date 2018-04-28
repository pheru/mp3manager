package de.pheru.media.core.io.file;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonIO implements FileIO {

    private static final Logger LOGGER = LogManager.getLogger(JsonIO.class);

    private final Map<Class, JsonDeserializer> jsonDeserializers = new HashMap<>();
    private final Map<Class, JsonSerializer> jsonSerializers = new HashMap<>();

    @Override
    public <T> T read(final File file, final Class<T> type) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final SimpleModule module = new SimpleModule();
        for (final Map.Entry<Class, JsonDeserializer> entry : jsonDeserializers.entrySet()) {
            module.addDeserializer(entry.getKey(), entry.getValue());
        }
        mapper.registerModule(module);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        return mapper.readValue(file, type);
    }

    @Override
    public <T> void write(final File file, final Class<T> type, final T t) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final SimpleModule module = new SimpleModule();
        for (final Map.Entry<Class, JsonSerializer> entry : jsonSerializers.entrySet()) {
            module.addSerializer(entry.getKey(), entry.getValue());
        }
        mapper.registerModule(module);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new IOException("Failed to create parent directories for file " + file);
            }
        }
        mapper.writeValue(file, t);
    }

    public <T> void addJsonSerializer(final Class<T> clazz, final JsonSerializer<T> serializer) {
        jsonSerializers.put(clazz, serializer);
    }

    public <T> void addJsonDeserializer(final Class<T> clazz, final JsonDeserializer<T> serializer) {
        jsonDeserializers.put(clazz, serializer);
    }

}
