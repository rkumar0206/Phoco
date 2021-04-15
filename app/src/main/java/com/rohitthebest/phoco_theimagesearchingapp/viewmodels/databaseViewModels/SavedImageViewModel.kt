package com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.SavedImage
import com.rohitthebest.phoco_theimagesearchingapp.repositories.SavedImageRepository
import kotlinx.coroutines.launch

class SavedImageViewModel @ViewModelInject constructor(
        val repository: SavedImageRepository
) : ViewModel() {

    fun insertImage(savedImage: SavedImage) = viewModelScope.launch {

        repository.insertImage(savedImage)
    }

    fun insertImages(savedImages: List<SavedImage>) = viewModelScope.launch {

        repository.insertImages(savedImages)
    }


    fun deleteImage(savedImage: SavedImage) = viewModelScope.launch {

        repository.deleteImage(savedImage)
    }

    fun deleteAllImage() = viewModelScope.launch {

        repository.deleteAllImage()
    }

    fun deleteImageByImageId(imageId: String) = viewModelScope.launch {

        repository.deleteImageByImageId(imageId)
    }

    fun getAllSavedImages() = repository.getAllSavesImages().asLiveData()

    fun getSavedImagesByCollectionKey(collectionKey: String) =
            repository.getSavedImagesByCollectionKey(collectionKey).asLiveData()

    fun getSavedImageByImageId(id: String) = repository.getSavedImageByImageId(id).asLiveData()
}