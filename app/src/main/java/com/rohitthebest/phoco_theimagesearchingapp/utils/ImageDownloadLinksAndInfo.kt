package com.rohitthebest.phoco_theimagesearchingapp.utils

data class ImageDownloadLinksAndInfo(
        var imageUrls: ImageUrls,
        var imageName: String
) {

    data class ImageUrls(
            var small: String,
            var medium: String,
            var original: String
    )
}