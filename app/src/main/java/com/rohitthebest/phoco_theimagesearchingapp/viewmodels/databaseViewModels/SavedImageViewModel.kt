package com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.SavedImage
import com.rohitthebest.phoco_theimagesearchingapp.repositories.SavedImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedImageViewModel @Inject constructor(
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

    fun deleteAllByKey(keys: List<String>) = viewModelScope.launch {

        repository.deleteAllByKey(keys)
    }

    fun deleteImageByImageId(imageId: String) = viewModelScope.launch {

        repository.deleteImageByImageId(imageId)
    }

    fun deleteByCollectionKey(collectionKey: String) = viewModelScope.launch {

        repository.deleteByCollectionKey(collectionKey)
    }

    fun getAllSavedImages() = repository.getAllSavesImages().asLiveData()

    fun getSavedImagesByCollectionKey(collectionKey: String) =
            repository.getSavedImagesByCollectionKey(collectionKey).asLiveData()

    fun getSavedImageByImageId(id: String) = repository.getSavedImageByImageId(id).asLiveData()

    fun getAllSavedImagesID() = repository.getAllSavedImagesID().asLiveData()

    fun getAllSavedImagesByListOfKeys(listOfKeys: List<String>) = repository.getAllSavedImagesByListOfKeys(
            listOfKeys
    ).asLiveData()
}