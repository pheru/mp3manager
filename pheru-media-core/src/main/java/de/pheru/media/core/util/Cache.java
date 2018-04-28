package de.pheru.media.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public List<T> get(final CacheMatcher<T> cacheMatcher) {
        final List<T> matches = new ArrayList<>();
        for (final T t : cache.values()) {
            if (cacheMatcher.match(t)) {
                matches.add(t);
            }
        }
        return matches;
    }

    public Integer getKey(final T item) {
        for (final Map.Entry<Integer, T> entry : cache.entrySet()) {
            if (entry.getValue().equals(item)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public interface CacheMatcher<T> {
        boolean match(final T cacheItem);
    }
}
