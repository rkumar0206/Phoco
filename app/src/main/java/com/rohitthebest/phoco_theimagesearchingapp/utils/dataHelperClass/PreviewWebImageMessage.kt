package com.rohitthebest.phoco_theimagesearchingapp.utils.dataHelperClass

import com.rohitthebest.phoco_theimagesearchingapp.remote.mohitImagApiData.WebPhoto

data class PreviewWebImageMessage(
    val webImages: List<WebPhoto>,
    val selectedPosition: Int
)
