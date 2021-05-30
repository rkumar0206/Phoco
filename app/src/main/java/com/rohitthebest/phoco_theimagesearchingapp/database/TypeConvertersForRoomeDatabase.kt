package com.rohitthebest.phoco_theimagesearchingapp.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.UserInfo
import com.rohitthebest.phoco_theimagesearchingapp.remote.unsplashData.UnsplashPhoto
import com.rohitthebest.phoco_theimagesearchingapp.utils.APIsInfo
import com.rohitthebest.phoco_theimagesearchingapp.utils.ImageDownloadLinksAndInfo

class TypeConvertersForRoomeDatabase {

    val gson = Gson()

    @TypeConverter
    fun convertUnsplashPhotoUrlsToString(unsplashPhotoUrl: UnsplashPhoto.UnsplashPhotoUrls): String {

        return gson.toJson(unsplashPhotoUrl)
    }

    @TypeConverter
    fun convertStringToUnsplashPhotoUrl(str: String): UnsplashPhoto.UnsplashPhotoUrls {

        val type = object : TypeToken<UnsplashPhoto.UnsplashPhotoUrls>() {}.type
        return gson.fromJson(str, type)
    }

    @TypeConverter
    fun convertUnsplashLinksToString(unsplashLinks: UnsplashPhoto.Links): String {

        return gson.toJson(unsplashLinks)
    }

    @TypeConverter
    fun convertStringToUnsplashLink(str: String): UnsplashPhoto.Links {

        val type = object : TypeToken<UnsplashPhoto.Links>() {}.type
        return gson.fromJson(str, type)
    }

    @TypeConverter
    fun convertUnsplashUserToString(unsplashUser: UnsplashPhoto.UnsplashUser): String {

        return gson.toJson(unsplashUser)
    }

    @TypeConverter
    fun convertStringToUnsplashUser(str: String): UnsplashPhoto.UnsplashUser {

        val type = object : TypeToken<UnsplashPhoto.UnsplashUser>() {}.type
        return gson.fromJson(str, type)
    }


    @TypeConverter
    fun convertApiInfoToString(apIsInfo: APIsInfo): String {

        return gson.toJson(apIsInfo)
    }

    @TypeConverter
    fun convertStringToApiInfo(str: String): APIsInfo {

        val type = object : TypeToken<APIsInfo>() {}.type
        return gson.fromJson(str, type)
    }

    @TypeConverter
    fun convertImageUrlToString(imageUrls: ImageDownloadLinksAndInfo.ImageUrls): String {

        return gson.toJson(imageUrls)
    }

    @TypeConverter
    fun convertStringToImageUrl(str: String): ImageDownloadLinksAndInfo.ImageUrls {

        val type = object : TypeToken<ImageDownloadLinksAndInfo.ImageUrls>() {}.type
        return gson.fromJson(str, type)
    }

    @TypeConverter
    fun convertUserInfoToString(userInfo: UserInfo): String {

        return gson.toJson(userInfo)
    }

    @TypeConverter
    fun convertStringToUserInfo(str: String): UserInfo {

        val type = object : TypeToken<UserInfo>() {}.type
        return gson.fromJson(str, type)
    }

}