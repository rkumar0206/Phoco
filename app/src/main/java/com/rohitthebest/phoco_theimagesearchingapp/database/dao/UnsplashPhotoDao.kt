package com.rohitthebest.phoco_theimagesearchingapp.database.dao

import androidx.room.*
import com.rohitthebest.phoco_theimagesearchingapp.data.unsplashData.UnsplashPhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface UnsplashPhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnsplashPhoto(unsplashPhoto: UnsplashPhoto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnsplashPhotoList(unsplashPhotoList: List<UnsplashPhoto>)

    @Update
    suspend fun updateUnsplashPhoto(unsplashPhoto: UnsplashPhoto)

    @Delete
    suspend fun deleteUNsplashPhoto(unsplashPhoto: UnsplashPhoto)

    @Query("DELETE FROM unsplash_image_table")
    suspend fun deleteAllUnsplashPhoto()

    @Query("SELECT * FROM unsplash_image_table")
    fun getAllUnsplashPhoto(): Flow<List<UnsplashPhoto>>

    @Query("SELECT * FROM unsplash_image_table WHERE id = :id")
    fun getPhotoById(id: String): Flow<UnsplashPhoto>
}