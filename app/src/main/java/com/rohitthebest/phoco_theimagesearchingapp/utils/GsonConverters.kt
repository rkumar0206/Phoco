package com.rohitthebest.phoco_theimagesearchingapp.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GsonConverters {

    companion object {

        val gson = Gson()

        fun convertImageDownloadLinksAndInfoToString(imageDownloadLinksAndInfo: ImageDownloadLinksAndInfo): String {

            return gson.toJson(imageDownloadLinksAndInfo)
        }

        fun convertStringToImageDownloadLinksAndInfo(str: String): ImageDownloadLinksAndInfo {

            val type = object : TypeToken<ImageDownloadLinksAndInfo>() {}.type
            return gson.fromJson(str, type)
        }


    }
}