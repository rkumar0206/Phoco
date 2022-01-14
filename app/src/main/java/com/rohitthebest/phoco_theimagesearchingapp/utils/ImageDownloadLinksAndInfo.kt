package com.rohitthebest.phoco_theimagesearchingapp.utils

data class ImageDownloadLinksAndInfo(
    var imageUrls: ImageUrls,
    var imageName: String,
    var imageId: String = ""  //it's mandatory to pass id if the tag is HomeFragment
) {

    data class ImageUrls(
            var small: String,
            var medium: String,
            var original: String
    ) {
        constructor() : this(
                "",
                "",
                ""
        )
    }
}