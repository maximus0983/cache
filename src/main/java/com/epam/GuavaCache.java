package com.epam;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;

import java.util.concurrent.TimeUnit;

public class GuavaCache {
    private CacheLoader<String, String> loader = new CacheLoader<>() {
        @Override
        public String load(String s) {
            return s;
        }
    };

    private LoadingCache<String, String> cache;

    public GuavaCache(TypeStrategy type, long value) {
        switch (type) {
            case SIZE:
                cache = CacheBuilder.newBuilder().maximumSize(value).build(loader);
                break;
            case TIME:
                cache = CacheBuilder.newBuilder()
                        .expireAfterAccess(value, TimeUnit.SECONDS)
                        .build(loader);
                break;
            case WEIGHT:
                Weigher<String, String> weighByLength;
                weighByLength = (key, value1) -> value1.length();
                cache = CacheBuilder.newBuilder()
                        .maximumWeight(value)
                        .weigher(weighByLength)
                        .build(loader);
                break;
        }
    }

    public Instanse get(String key) {
        return new Instanse(cache.getUnchecked(key));
    }

    public LoadingCache<String, String> getCache() {
        return cache;
    }
}

enum TypeStrategy {
    TIME, WEIGHT, SIZE
}

