package com.rohitthebest.phoco_theimagesearchingapp.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rohitthebest.phoco_theimagesearchingapp.database.TypeConvertersForRoomeDatabase
import com.rohitthebest.phoco_theimagesearchingapp.database.dao.UnsplashPhotoDao
import com.rohitthebest.phoco_theimagesearchingapp.remote.unsplashData.UnsplashPhoto

@Database(entities = [UnsplashPhoto::class], version = 1)
@TypeConverters(TypeConvertersForRoomeDatabase::class)
abstract class UnsplashPhotoDatabase : RoomDatabase() {

    abstract fun getUnsplashPhotoDao(): UnsplashPhotoDao
}