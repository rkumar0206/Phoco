package com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rohitthebest.phoco_theimagesearchingapp.data.unsplashData.UnsplashPhoto
import com.rohitthebest.phoco_theimagesearchingapp.repositories.UnsplashPhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnsplashPhotoViewModel @Inject constructor(
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

    fun deleteUnsplashPhoto(unsplashPhoto: UnsplashPhoto) = viewModelScope.launch {

        repository.deleteUNsplashPhoto(unsplashPhoto)
    }

    fun deleteAllUnsplashPhoto() = viewModelScope.launch {

        repository.deleteAllUnsplashPhoto()
    }

    fun getAllUnsplashPhoto() = repository.getAllUnsplashPhoto().asLiveData()

    fun getPhotoById(id: String) = repository.getPhotoById(id).asLiveData()
}