package com.rohitthebest.phoco_theimagesearchingapp.remote.phocoData

import com.google.gson.annotations.SerializedName

data class PhocoImageItem(
    val created: String,
    val height: Int,
    val image: Image,
    val image_description: String,
    val phoco_user: Int,
    val pk: Int,
    val updated: String,
    val width: Int,
    val user: User
) {

    data class Image(
        val full_size: String,
        val medium: String,
        val small: String,
        val thumbnail: String
    )

    data class User(
        val username: String,
        val name: String,
        @SerializedName("user_id") val userId: String,
        @SerializedName("profile_url") val profileUrl: String? = null,
        @SerializedName("image_url") val imageUrl: String? = null
    )

}