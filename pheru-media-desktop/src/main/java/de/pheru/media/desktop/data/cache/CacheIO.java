package de.pheru.media.desktop.data.cache;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface CacheIO {

    void writeCacheFile(final File file, final Cache cache) throws IOException;

    Cache readCacheFile(final File file) throws IOException;
}
