package de.pheru.media.core.io.file;

import java.io.File;
import java.io.IOException;

public interface FileIO {

    <T> T read(final File file, final Class<T> type) throws IOException;

    <T> void write(final File file, final Class<T> type, final T t, final String localPart) throws IOException;
}
