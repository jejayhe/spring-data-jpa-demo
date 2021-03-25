package com.example.demo.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class LocalCache {
    public static LoadingCache<Long, String> CustomerLocalCache = Caffeine.newBuilder()
            .maximumSize(5)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(k -> new Date().toString());

}
