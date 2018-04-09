package de.pheru.media.core.util;

import java.util.HashMap;
import java.util.Map;

public class Cache<T> {

    private final Map<Integer, T> cache = new HashMap<>();
    private Integer currentKey = 0;

    public Integer add(final T item) {
        currentKey++;
        cache.put(currentKey, item);
        return currentKey;
    }

    public T get(final Integer key) {
        return cache.get(key);
    }

    public T get(final CacheCheck<T> cacheCheck) {
        for (final T t : cache.values()) {
            if (cacheCheck.execute(t)) {
                return t;
            }
        }
        return null;
    }

    public Integer getKey(final T item) {
        for (final Map.Entry<Integer, T> entry : cache.entrySet()) {
            if (entry.getValue().equals(item)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public interface CacheCheck<T> {
        boolean execute(final T cacheItem);
    }
}
