package com.rohitthebest.phoco_theimagesearchingapp.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.SavedImage
import com.rohitthebest.phoco_theimagesearchingapp.utils.dataHelperClass.ImageDownloadLinksAndInfo
import com.rohitthebest.phoco_theimagesearchingapp.utils.dataHelperClass.PreviewUnDrawImagesMessage
import com.rohitthebest.phoco_theimagesearchingapp.utils.dataHelperClass.PreviewWebImageMessage

class GsonConverters {

    companion object {

        private val gson = Gson()

        fun convertImageDownloadLinksAndInfoToString(imageDownloadLinksAndInfo: ImageDownloadLinksAndInfo): String {

            return gson.toJson(imageDownloadLinksAndInfo)
        }

        fun convertStringToImageDownloadLinksAndInfo(str: String): ImageDownloadLinksAndInfo {

            val type = object : TypeToken<ImageDownloadLinksAndInfo>() {}.type
            return gson.fromJson(str, type)
        }

        fun fromPreviewWebImageMessageToString(previewWebImageMessage: PreviewWebImageMessage): String {

            return Gson().toJson(previewWebImageMessage)
        }

        fun fromStringToPreviewWebImageMessage(str: String): PreviewWebImageMessage {

            return Gson().fromJson(str, object : TypeToken<PreviewWebImageMessage>() {}.type)
        }

        fun fromPreviewUnDrawImagesMessageToString(previewUnDrawImagesMessage: PreviewUnDrawImagesMessage): String {

            return Gson().toJson(previewUnDrawImagesMessage)
        }

        fun fromStringToPreviewUnDrawImagesMessage(str: String): PreviewUnDrawImagesMessage {

            return Gson().fromJson(str, object : TypeToken<PreviewUnDrawImagesMessage>() {}.type)
        }

        fun convertSavedImageToString(savedImage: SavedImage): String {

            return gson.toJson(savedImage)
        }

        fun convertStringToSavedImage(str: String): SavedImage {

            val type = object : TypeToken<SavedImage>() {}.type
            return gson.fromJson(str, type)
        }

        fun convertListOfStringString(strings: List<String>): String {

            return gson.toJson(strings)
        }

        fun convertStringToListOfStrings(str: String): List<String> {

            val type = object : TypeToken<List<String>>() {}.type
            return gson.fromJson(str, type)
        }
    }
}