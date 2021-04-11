package com.rohitthebest.phoco_theimagesearchingapp.utils

enum class APIName {

    UNSPLASH,
    PIXABAY,
    PEXELS,
    WEB
}

data class APIsInfo(
        var apiName: APIName,
        var apiImage: Int
) {

    constructor() : this(
            APIName.UNSPLASH,
            0
    )
}