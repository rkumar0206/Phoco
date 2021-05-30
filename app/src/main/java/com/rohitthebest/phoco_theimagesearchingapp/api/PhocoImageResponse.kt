package com.rohitthebest.phoco_theimagesearchingapp.api

import com.rohitthebest.phoco_theimagesearchingapp.data.phocoData.PhocoImageItem

data class PhocoImageResponse(
    val count: Int,
    val next: String,
    val previous: Any,
    val results: List<PhocoImageItem>
)