package com.rohitthebest.phoco_theimagesearchingapp.utils

data class ImageDownloadLinksAndInfo(
        var imageUrls: ImageUrls,
        var imageName: String,
        var tag: String,
        var imageId: String? = null  //it's mandatory to pass id if the tag is HomeFragment
) {

    data class ImageUrls(
            var small: String,
            var medium: String,
            var original: String
    )
}