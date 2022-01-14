package com.rohitthebest.phoco_theimagesearchingapp.utils

import com.rohitthebest.phoco_theimagesearchingapp.remote.undrawData.Illo
import java.io.Serializable

data class PreviewUnDrawImagesMessage(
    val unDrawImages: List<Illo>,
    val selectedPosition: Int
) : Serializable
