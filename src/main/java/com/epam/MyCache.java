package com.epam;

import lombok.Data;
import lombok.extern.java.Log;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Log
public class MyCache {
    private int maxSize;
    private int expirationTime;
    private int capacity;

    private long average;

    private Instanse lastAdded;

    private ConcurrentHashMap<String, Integer> usage = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Long> timeUsage = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, Instanse> cacheList = new ConcurrentHashMap<>();

    public MyCache(int maxSize, int expirationTime, int capacity) {
        this.maxSize = maxSize;
        this.expirationTime = expirationTime;
        this.capacity = capacity;
    }

    public MyCache() {
    }

    public int cacheSize() {
        return cacheList.size();
    }

    public long getAverage() {
        return average;
    }

    public Instanse get(Instanse instanse) {
        if (cacheList.containsKey(instanse.getName())) {
            int count = Optional.ofNullable(usage.get(instanse.getName())).orElse(0);
            usage.put(instanse.getName(), count + 1);
            return cacheList.get(instanse.getName());
        }
        return null;
    }

    public Instanse put(Instanse instanse) {
        long start = System.currentTimeMillis();
        if (capacity != 0 && cacheList.size() == capacity) {
            Optional<Map.Entry<String, Integer>> min = usage.entrySet().stream()
                    .min(Comparator.comparing(Map.Entry::getValue));
            if (min.isPresent()) {
                removeCache(min.get().getKey());

            } else if (lastAdded != null) {
                removeCache(lastAdded.getName());
            }
        }
        if (expirationTime > 0) {
            return addAndRemoveIfTimeExpired(instanse, start);
        } else {
            lastAdded = instanse;
            Instanse put = cacheList.put(instanse.getName(), instanse);
            usage.put(instanse.getName(), 0);
            long end = System.currentTimeMillis();
            average = (average + end - start) / cacheSize();
            return put;
        }
    }

    private void removeCache(String key) {
        cacheList.remove(key);
        usage.remove(key);
        log.info("Cache entry removed " + key);
    }

    private Instanse addAndRemoveIfTimeExpired(Instanse instanse, long start) {
        new Thread(() -> {
            try {
                long currentTime = System.currentTimeMillis();
                timeUsage.put(instanse.getName(), currentTime);
                cacheList.put(instanse.getName(), instanse);
                long end = System.currentTimeMillis();
                average = (average + end - start) / cacheSize();
                Thread.sleep(expirationTime);
                timeUsage.remove(instanse.getName());
                cacheList.remove(instanse.getName());
                removeCache(instanse.getName());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        return instanse;
    }
}
