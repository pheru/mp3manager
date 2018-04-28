package de.pheru.media.core.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class CacheTest {

    @Test
    public void addAndGet() {
        final Cache<String> cache = new Cache<>();
        final String value1 = "String1";
        final String value2 = "String2";

        Integer key1 = cache.add(value1);
        Integer key2 = cache.add(value2);
        assertEquals(value1, cache.get(key1));
        assertEquals(value2, cache.get(key2));
    }

    @Test
    public void getWithMatcher() {
        final Cache<String> cache = new Cache<>();
        final String value1 = "String1";
        final String value2 = "String2";

        cache.add(value1);
        cache.add(value2);

        assertEquals(1, cache.get(cacheItem -> cacheItem.equals(value1)).size());
        assertEquals(value1, cache.get(cacheItem -> cacheItem.equals(value1)).get(0));
        assertEquals(2, cache.get(cacheItem -> cacheItem.startsWith("String")).size());
    }

    @Test
    public void getKey() {
        final Cache<String> cache = new Cache<>();
        final String value1 = "String1";

        Integer key1 = cache.add(value1);
        assertEquals(key1, cache.getKey(value1));
    }
}