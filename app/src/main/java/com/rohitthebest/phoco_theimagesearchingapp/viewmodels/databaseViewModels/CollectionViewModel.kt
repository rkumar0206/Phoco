package com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.Collection
import com.rohitthebest.phoco_theimagesearchingapp.repositories.CollectionRepository
import com.rohitthebest.phoco_theimagesearchingapp.repositories.SavedImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CollectionViewModel"

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val repository: CollectionRepository,
    private val savedImageRepository: SavedImageRepository
) : ViewModel() {

    fun insertCollection(collection: Collection) = viewModelScope.launch {

        repository.insertCollection(collection)
    }

    fun insertCollections(collections: List<Collection>) = viewModelScope.launch {

        repository.insertCollections(collections)
    }

    fun updateCollection(collection: Collection) = viewModelScope.launch {

        repository.updateCollection(collection)
    }

    fun deleteCollection(collection: Collection) = viewModelScope.launch {

        repository.deleteCollection(collection)
    }

    fun deleteAllCollection() = viewModelScope.launch {

        repository.deleteAllCollection()
    }

    fun getAllCollection() = repository.getAllCollection().asLiveData()

    fun getCollectionByKey(key: String) = repository.getCollectionByKey(key).asLiveData()

    fun getCollectionByName(collectionName: String) =
        repository.getCollectionByName(collectionName).asLiveData()

    // delete all the collections with no saved image inside it
    fun deleteAllEmptyCollections() {

        Log.i(TAG, "inside deleteAllEmptyCollections: ")

        viewModelScope.launch {

            repository.getAllCollection().map { collections ->

                collections.map { collection ->
                    async {

                        try {// if any collection is found without any saved image then it will br deleted
                            if (savedImageRepository.getSavedImagesByCollectionKey(collection.key)
                                    .first().isEmpty()
                            ) {

                                repository.deleteCollection(collection)
                            }
                        } catch (e: NoSuchElementException) {
                            e.printStackTrace()
                        }
                    }
                }.awaitAll()

            }.collect {}
        }

    }
}