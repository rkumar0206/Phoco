package com.rohitthebest.phoco_theimagesearchingapp.repositories

import com.rohitthebest.phoco_theimagesearchingapp.database.dao.SavedImageDao
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.SavedImage
import javax.inject.Inject

class SavedImageRepository @Inject constructor(
        val dao: SavedImageDao
) {

    suspend fun insertImage(savedImage: SavedImage) = dao.insertImage(savedImage)

    suspend fun insertImages(savedImages: List<SavedImage>) = dao.insertImages(savedImages)

    suspend fun deleteImage(savedImage: SavedImage) = dao.deleteImage(savedImage)

    suspend fun deleteAllImage() = dao.deleteAllImage()

    fun getAllSavesImages() = dao.getAllSavesImages()

    fun getSavedImagesByCollectionKey(collectionKey: String) = dao.getSavedImagesByCollectionKey(collectionKey)

}