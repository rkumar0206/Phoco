package com.rohitthebest.phoco_theimagesearchingapp.api

import com.rohitthebest.phoco_theimagesearchingapp.remote.pixabayData.PixabayPhoto

data class PixabayResponse(
        val hits: List<PixabayPhoto>,
        val total: Int,
        val totalHits: Int
)