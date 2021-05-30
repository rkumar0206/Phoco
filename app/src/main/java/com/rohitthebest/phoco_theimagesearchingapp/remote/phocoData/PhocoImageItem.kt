package com.rohitthebest.phoco_theimagesearchingapp.remote.phocoData

data class PhocoImageItem(
    val created: String,
    val height: Int,
    val image: Image,
    val image_description: String,
    val phoco_user: Int,
    val pk: Int,
    val updated: String,
    val width: Int
) {

    data class Image(
        val full_size: String,
        val medium: String,
        val small: String,
        val thumbnail: String
    )
}