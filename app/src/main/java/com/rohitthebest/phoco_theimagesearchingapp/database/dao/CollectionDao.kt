package com.rohitthebest.phoco_theimagesearchingapp.database.dao

import androidx.room.*
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.Collection
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: Collection)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollections(collections: List<Collection>)

    @Delete
    suspend fun deleteCollection(collection: Collection)

    @Query("DELETE FROM collection_table")
    suspend fun deleteAllCollection()

    @Query("SELECT * FROM collection_table ORDER BY timestamp DESC")
    fun getAllCollections(): Flow<List<Collection>>

    @Query("SELECT * FROM collection_table WHERE `key` =:key")
    fun getCollectionByKey(key: String): Flow<Collection>
}