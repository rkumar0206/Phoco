package com.rohitthebest.phoco_theimagesearchingapp.repositories

import com.rohitthebest.phoco_theimagesearchingapp.database.dao.CollectionDao
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.Collection
import javax.inject.Inject

class CollectionRepository @Inject constructor(
        val dao: CollectionDao
) {

    suspend fun insertCollection(collection: Collection) = dao.insertCollection(collection)

    suspend fun insertCollections(collections: List<Collection>) = dao.insertCollections(collections)

    suspend fun deleteCollection(collection: Collection) = dao.deleteCollection(collection)

    suspend fun deleteAllCollection() = dao.deleteAllCollection()

    fun getAllCollection() = dao.getAllCollections()

    fun getCollectionByKey(key: String) = dao.getCollectionByKey(key)
}