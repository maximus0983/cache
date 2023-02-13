package com.epam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MyCacheTest {
    private MyCache myCache;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        myCache = new MyCache();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        myCache = null;
    }

    @org.junit.jupiter.api.Test
    void getCacheWhenCapacityEqual3() {
        myCache.setCapacity(3);
        myCache.put(new Instanse("s1"));
        myCache.put(new Instanse("s2"));
        myCache.put(new Instanse("s3"));
        myCache.get(new Instanse("s3"));
        myCache.get(new Instanse("s2"));
        myCache.put(new Instanse("s4"));
        assertEquals(3, myCache.cacheSize());
        assertNull(myCache.get(new Instanse("s1")));
    }

    @org.junit.jupiter.api.Test
    void getCacheWhenTime5sek() throws InterruptedException {
        myCache.setExpirationTime(5000);
        myCache.put(new Instanse("s1"));
        myCache.put(new Instanse("s2"));
        myCache.put(new Instanse("s3"));
        myCache.put(new Instanse("s4"));
        assertEquals(4, myCache.cacheSize());
        Thread.sleep(11000);
        assertEquals(0, myCache.cacheSize());
    }
}