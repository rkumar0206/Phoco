package com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.Collection
import com.rohitthebest.phoco_theimagesearchingapp.repositories.CollectionRepository
import kotlinx.coroutines.launch

class CollectionViewModel @ViewModelInject constructor(
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

    fun getAllCollection() = repository.getAllCollection()

    fun getCollectionByKet(key: String) = repository.getCollectionByKey(key)

}