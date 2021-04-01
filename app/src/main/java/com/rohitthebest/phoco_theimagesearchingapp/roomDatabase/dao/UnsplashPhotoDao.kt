package com.rohitthebest.phoco_theimagesearchingapp.roomDatabase.dao

import androidx.room.*
import com.rohitthebest.phoco_theimagesearchingapp.data.unsplashData.UnsplashPhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface UnsplashPhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnsplashPhoto(unsplashPhoto: UnsplashPhoto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnsplashPhotoList(unsplashPhotoList: List<UnsplashPhoto>)

    @Delete
    suspend fun deleteUNsplashPhoto(unsplashPhoto: UnsplashPhoto)

    @Query("DELETE FROM unsplash_image_table")
    suspend fun deleteAllUnsplashPhoto()

    @Query("SELECT * FROM unsplash_image_table")
    fun getAllUnsplashPhoto(): Flow<List<UnsplashPhoto>>
}