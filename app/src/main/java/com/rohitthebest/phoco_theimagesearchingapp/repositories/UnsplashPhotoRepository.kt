package com.rohitthebest.phoco_theimagesearchingapp.repositories

import com.rohitthebest.phoco_theimagesearchingapp.data.unsplashData.UnsplashPhoto
import com.rohitthebest.phoco_theimagesearchingapp.roomDatabase.dao.UnsplashPhotoDao
import javax.inject.Inject

class UnsplashPhotoRepository @Inject constructor(
        val dao: UnsplashPhotoDao
) {

    suspend fun insertUnsplashPhoto(unsplashPhoto: UnsplashPhoto) = dao.insertUnsplashPhoto(unsplashPhoto)

    suspend fun insertUnsplashPhotoList(unsplashPhotoList: List<UnsplashPhoto>) = dao.insertUnsplashPhotoList(unsplashPhotoList)

    suspend fun deleteUNsplashPhoto(unsplashPhoto: UnsplashPhoto) = dao.deleteUNsplashPhoto(unsplashPhoto)

    suspend fun deleteAllUnsplashPhoto() = dao.deleteAllUnsplashPhoto()

    fun getAllUnsplashPhoto() = dao.getAllUnsplashPhoto()
}