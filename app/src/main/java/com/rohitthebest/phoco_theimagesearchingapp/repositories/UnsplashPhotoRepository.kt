package com.rohitthebest.phoco_theimagesearchingapp.repositories

import com.rohitthebest.phoco_theimagesearchingapp.database.dao.UnsplashPhotoDao
import com.rohitthebest.phoco_theimagesearchingapp.remote.unsplashData.UnsplashPhoto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashPhotoRepository @Inject constructor(
    val dao: UnsplashPhotoDao
) {

    suspend fun insertUnsplashPhoto(unsplashPhoto: UnsplashPhoto) = dao.insertUnsplashPhoto(unsplashPhoto)

    suspend fun insertUnsplashPhotoList(unsplashPhotoList: List<UnsplashPhoto>) = dao.insertUnsplashPhotoList(unsplashPhotoList)

    suspend fun updateUnsplashPhoto(unsplashPhoto: UnsplashPhoto) = dao.updateUnsplashPhoto(unsplashPhoto)

    suspend fun deleteUNsplashPhoto(unsplashPhoto: UnsplashPhoto) = dao.deleteUNsplashPhoto(unsplashPhoto)

    suspend fun deleteAllUnsplashPhoto() = dao.deleteAllUnsplashPhoto()

    fun getAllUnsplashPhoto() = dao.getAllUnsplashPhoto()

    fun getPhotoById(id: String) = dao.getPhotoById(id)
}