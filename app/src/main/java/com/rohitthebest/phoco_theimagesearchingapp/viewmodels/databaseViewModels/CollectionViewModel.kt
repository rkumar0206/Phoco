package com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.Collection
import com.rohitthebest.phoco_theimagesearchingapp.repositories.CollectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    val repository: CollectionRepository
) : ViewModel() {

    fun insertCollection(collection: Collection) = viewModelScope.launch {

        repository.insertCollection(collection)
    }

    fun insertCollections(collections: List<Collection>) = viewModelScope.launch {

        repository.insertCollections(collections)
    }

    fun deleteCollection(collection: Collection) = viewModelScope.launch {

        repository.deleteCollection(collection)
    }

    fun deleteAllCollection() = viewModelScope.launch {

        repository.deleteAllCollection()
    }

    fun getAllCollection() = repository.getAllCollection().asLiveData()

    fun getCollectionByKey(key: String) = repository.getCollectionByKey(key).asLiveData()

    fun getCollectionByName(collectionName: String) = repository.getCollectionByName(collectionName).asLiveData()
}