package com.rohitthebest.phoco_theimagesearchingapp.remote.pixabayData

data class PixabayPhoto(
        var comments: Int,
        var downloads: Int,
        var favorites: Int,
        var id: Int,
        var imageHeight: Int,
        var imageSize: Int,
        var imageWidth: Int,
        var largeImageURL: String,
        var likes: Int,
        var pageURL: String,
        var previewHeight: Int,
        var previewURL: String,
        var previewWidth: Int,
        var tags: String,
        var type: String,
        var user: String,
        var userImageURL: String,
        var user_id: Int,
        var views: Int,
        var webformatHeight: Int,
        var webformatURL: String,
        var webformatWidth: Int
) {
        constructor() : this(
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                "",
                0,
                "",
                0,
                "",
                0,
                "", "", "", "", 0, 0, 0, "", 0

        )

        val attributionUrl get() = "https://pixabay.com/users/$user-$user_id/"
}