package com.cs407.secondserve.util

import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.toolbox.ImageLoader.ImageCache

class AppImageCache(maxCacheSize: Int) : ImageCache {
    private val lruCache: LruCache<String, Bitmap> = LruCache<String, Bitmap>(maxCacheSize)

    override fun putBitmap(url: String, bitmap: Bitmap) {
        lruCache.put(url, bitmap)
    }

    override fun getBitmap(url: String): Bitmap? {
        return lruCache.get(url)
    }
}