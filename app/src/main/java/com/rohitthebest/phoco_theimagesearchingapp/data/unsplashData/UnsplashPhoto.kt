package com.rohitthebest.phoco_theimagesearchingapp.data.unsplashData

import androidx.room.Entity
import androidx.room.PrimaryKey

//making this as an entity class for saving the list which is displayed in the HomeFragment
@Entity(tableName = "unsplash_image_table")
data class UnsplashPhoto(
        @PrimaryKey val id: String,
        val width: Int,
        val height: Int,
        val color: String,
        val alt_description: String? = null,
        val urls: UnsplashPhotoUrls,
        val links: Links,
        val user: UnsplashUser,
        var isImageSaved : Boolean = false
) {

    data class UnsplashPhotoUrls(
            val raw: String,
            val full: String,
            val regular: String,
            val small: String,
            val thumb: String,
    )

    data class Links(
            val self: String?,
            val html: String,
            val download: String
    )

    data class UnsplashUser(
            val id: String,
            val name: String,
            val username: String,
            val profile_image: UnsplashProfileImage,
            val total_collections : Int,
            val total_photos : Int,

            ) {
        val attributionUrl get() = "https://unsplash.com/$username?utm_source=ImageSearchApp&utm_medium=referral"
    }

    data class UnsplashProfileImage(
            val small: String,
            val medium: String,
            val large: String
    )

}