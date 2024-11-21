package com.cs407.secondserve.util

import android.widget.ImageView.ScaleType
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader

class APIImageLoader(
    queue: RequestQueue?,
    imageCache: ImageCache?,
    val baseUrl: String
) : ImageLoader(queue, imageCache) {
    override fun get(
        requestUrl: String?,
        imageListener: ImageListener?,
        maxWidth: Int,
        maxHeight: Int,
        scaleType: ScaleType?
    ): ImageContainer? {
        val fullImageUrl = "$baseUrl/$requestUrl"
        return super.get(fullImageUrl, imageListener, maxWidth, maxHeight, scaleType)
    }
}