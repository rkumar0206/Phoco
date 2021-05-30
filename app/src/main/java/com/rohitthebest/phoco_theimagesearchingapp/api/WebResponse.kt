package com.rohitthebest.phoco_theimagesearchingapp.api

import com.rohitthebest.phoco_theimagesearchingapp.remote.mohitImagApiData.WebPhoto

data class WebResponse(
        val result: List<WebPhoto>,
        val query: String   //search String
)
