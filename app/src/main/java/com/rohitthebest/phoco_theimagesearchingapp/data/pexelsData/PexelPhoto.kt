package com.rohitthebest.phoco_theimagesearchingapp.data.pexelsData

data class PexelPhoto(
        val avg_color: String,
        val height: Int,
        val id: Int,
        val liked: Boolean,
        val photographer: String,
        val photographer_id: Int,
        val photographer_url: String,
        val src: Src,
        val url: String,
        val width: Int
)