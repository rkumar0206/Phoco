package com.rohitthebest.phoco_theimagesearchingapp.repositories

import com.rohitthebest.phoco_theimagesearchingapp.database.dao.SavedImagesDao
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.SavedImage
import javax.inject.Inject

class SavedImageRepository @Inject constructor(
        val dao: SavedImagesDao
) {

    suspend fun insertImage(savedImage: SavedImage) = dao.insertImage(savedImage)

    suspend fun insertImages(savedImages: List<SavedImage>) = dao.insertImages(savedImages)

    suspend fun deleteImage(savedImage: SavedImage) = dao.delete(savedImage)

    suspend fun deleteAllImage() = dao.deleteAll()

    suspend fun deleteImageByImageId(imageId: String) = dao.deleteImageByImageId(imageId)

    fun getAllSavesImages() = dao.getAllSavedImages()

    fun getSavedImagesByCollectionKey(collectionKey: String) = dao.getAllSavedImagesByCollectionKey(collectionKey)

}