package com.rohitthebest.phoco_theimagesearchingapp.api

import com.rohitthebest.phoco_theimagesearchingapp.data.pixabayData.PixabayData

data class PixabayResponse(
        val hits: List<PixabayData>,
        val total: Int,
        val totalHits: Int
)