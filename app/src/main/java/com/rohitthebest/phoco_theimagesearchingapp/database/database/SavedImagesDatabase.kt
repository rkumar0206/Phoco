package com.rohitthebest.phoco_theimagesearchingapp.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rohitthebest.phoco_theimagesearchingapp.database.TypeConvertersForRoomeDatabase
import com.rohitthebest.phoco_theimagesearchingapp.database.dao.SavedImagesDao
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.SavedImage

@Database(entities = [SavedImage::class], version = 1)
@TypeConverters(TypeConvertersForRoomeDatabase::class)
abstract class SavedImagesDatabase : RoomDatabase() {

    abstract fun getSavedImageDao(): SavedImagesDao
}