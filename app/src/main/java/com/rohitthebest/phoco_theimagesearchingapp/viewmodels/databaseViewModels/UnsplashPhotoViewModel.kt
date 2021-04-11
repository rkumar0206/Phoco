package com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rohitthebest.phoco_theimagesearchingapp.data.unsplashData.UnsplashPhoto
import com.rohitthebest.phoco_theimagesearchingapp.repositories.UnsplashPhotoRepository
import kotlinx.coroutines.launch

class UnsplashPhotoViewModel @ViewModelInject constructor(
        val repository: UnsplashPhotoRepository
) : ViewModel() {

    fun insertUnsplashPhoto(unsplashPhoto: UnsplashPhoto) = viewModelScope.launch {

        repository.insertUnsplashPhoto(unsplashPhoto)
    }

    fun insertUnsplashPhotoList(unsplashPhotoList: List<UnsplashPhoto>) = viewModelScope.launch {

        repository.insertUnsplashPhotoList(unsplashPhotoList)
    }

    fun updateUnsplashPhoto(unsplashPhoto: UnsplashPhoto) = viewModelScope.launch {

        repository.updateUnsplashPhoto(unsplashPhoto)
    }

    fun deleteUNsplashPhoto(unsplashPhoto: UnsplashPhoto) = viewModelScope.launch {

        repository.deleteUNsplashPhoto(unsplashPhoto)
    }

    fun deleteAllUnsplashPhoto() = viewModelScope.launch {

        repository.deleteAllUnsplashPhoto()
    }

    fun getAllUnsplashPhoto() = repository.getAllUnsplashPhoto().asLiveData()
}