package com.rohitthebest.phoco_theimagesearchingapp.database.dao

import androidx.room.*
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.SavedImage
import kotlinx.coroutines.flow.Flow
import java.security.Key

@Dao
interface SavedImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(savedImage: SavedImage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(savedImages: List<SavedImage>)

    @Delete
    suspend fun deleteImage(savedImage: SavedImage)

    @Query("DELETE FROM saved_image_table")
    suspend fun deleteAllImage()

    @Query("SELECT * FROM saved_image_table ORDER BY timeStamp DESC")
    fun getAllSavesImages(): Flow<List<SavedImage>>

    @Query("SELECT * FROM saved_image_table WHERE collectionKey = :collectionKey ORDER BY timeStamp DESC")
    fun getSavedImagesByCollectionKey(collectionKey: Key): Flow<List<SavedImage>>
}