package com.rohitthebest.phoco_theimagesearchingapp.api

import com.rohitthebest.phoco_theimagesearchingapp.remote.unsplashData.UnsplashPhoto

data class UnsplashResponse(
        val results: List<UnsplashPhoto>
)