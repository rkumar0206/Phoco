package com.rohitthebest.phoco_theimagesearchingapp.database.dao

import androidx.room.*
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.SavedImage
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedImagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(savedImage: SavedImage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(savedImages: List<SavedImage>)

    @Delete
    suspend fun delete(savedImage: SavedImage)

    @Query("DELETE FROM saved_image_table")
    suspend fun deleteAll()

    @Query("DELETE FROM saved_image_table WHERE imageId = :imageId")
    suspend fun deleteImageByImageId(imageId: String)

    @Query("SELECT * FROM saved_image_table ORDER BY timeStamp DESC")
    fun getAllSavedImages(): Flow<List<SavedImage>>

    @Query("SELECT * FROM saved_image_table WHERE collectionKey = :collectionKey ORDER BY timeStamp DESC")
    fun getAllSavedImagesByCollectionKey(collectionKey: String): Flow<List<SavedImage>>

    @Query("SELECT * FROM saved_image_table WHERE imageId =:id")
    fun getSavedImageByImageId(id: String): Flow<SavedImage>


}