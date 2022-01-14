package com.rohitthebest.phoco_theimagesearchingapp.utils

import com.rohitthebest.phoco_theimagesearchingapp.database.entity.UserInfo

data class ImageDownloadLinksAndInfo(
    var imageUrls: ImageUrls,
    var imageName: String,
    var imageId: String = "",  //it's mandatory to pass id if the tag is HomeFragment
    var userInfo: UserInfo? = null,
    var width: Int = 0,
    var height: Int = 0
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