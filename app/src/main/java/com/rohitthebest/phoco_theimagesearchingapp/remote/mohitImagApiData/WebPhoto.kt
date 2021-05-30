package com.rohitthebest.phoco_theimagesearchingapp.remote.mohitImagApiData

data class WebPhoto(
        val height: String?,   //height of the image
        val imgurl: String?,   //Original image Url
        val name: String?,     // name of the image
        val preview: String?,  // Preview Image Url (Small image)
        val rurl: String?,    //website where the original image belongs
        val size: String?,    //Size of the image
        val width: String?   //width of the image
)