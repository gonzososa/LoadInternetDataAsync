package com.gmail.gonzaloantonio.examples.loadinternetdataasync;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class MemoryCache {
    private static final LruCache<String, Bitmap> memCache;

    static {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        memCache = new LruCache<String, Bitmap>(cacheSize);
    }

    public static void addBitmapToMemoryCache (String key, Bitmap bitmap) {
        synchronized (memCache) {
            if (getBitmapFromMemoryCache (key) == null) {
                memCache.put(key, bitmap);
            }
        }
    }

    public static Bitmap getBitmapFromMemoryCache (String key) {
        return memCache.get (key);
    }
}
