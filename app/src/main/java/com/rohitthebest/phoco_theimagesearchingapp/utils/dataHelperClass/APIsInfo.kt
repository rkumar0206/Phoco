package com.rohitthebest.phoco_theimagesearchingapp.utils.dataHelperClass

enum class APIName {

    UNSPLASH,
    PIXABAY,
    PEXELS,
    WEB,
    UNDRAW
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