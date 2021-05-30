package com.rohitthebest.phoco_theimagesearchingapp.remote.pixabayData

data class PixabayPhoto(
        val comments: Int,
        val downloads: Int,
        val favorites: Int,
        val id: Int,
        val imageHeight: Int,
        val imageSize: Int,
        val imageWidth: Int,
        val largeImageURL: String,
        val likes: Int,
        val pageURL: String,
        val previewHeight: Int,
        val previewURL: String,
        val previewWidth: Int,
        val tags: String,
        val type: String,
        val user: String,
        val userImageURL: String,
        val user_id: Int,
        val views: Int,
        val webformatHeight: Int,
        val webformatURL: String,
        val webformatWidth: Int
) {
    val attributionUrl get() = "https://pixabay.com/users/$user-$user_id/"
}