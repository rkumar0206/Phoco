package com.rohitthebest.phoco_theimagesearchingapp.api

import com.rohitthebest.phoco_theimagesearchingapp.remote.pexelsData.PexelPhoto

data class PexelResponse(
        val next_page: String,
        val page: Int,
        val per_page: Int,
        val photos: List<PexelPhoto>,
        val total_results: Int
) {
}