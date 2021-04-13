package com.rohitthebest.phoco_theimagesearchingapp.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rohitthebest.phoco_theimagesearchingapp.database.dao.CollectionDao
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.Collection

@Database(entities = [Collection::class], version = 2)
abstract class CollectionDatabase : RoomDatabase() {

    abstract fun getCollectionDao(): CollectionDao
}