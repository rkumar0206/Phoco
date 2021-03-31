package com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.rohitthebest.phoco_theimagesearchingapp.data.Resources
import com.rohitthebest.phoco_theimagesearchingapp.data.unsplashData.UnsplashPhoto
import com.rohitthebest.phoco_theimagesearchingapp.data.unsplashData.UnsplashRepository
import kotlinx.coroutines.launch

class UnsplashViewModel @ViewModelInject constructor(
    private val repository: UnsplashRepository
) : ViewModel() {

    //for searching image
    private val currentQuery = MutableLiveData<String>()

    val unsplashSearchResult = currentQuery.switchMap {

        repository.getSearchResultsFromUnsplash(it).asLiveData().cachedIn(viewModelScope)
    }

    fun searchImage(query: String) {

        currentQuery.value = query
    }

    //for getting image by id
    private val _unplashImageById = MutableLiveData<Resources<UnsplashPhoto>>()

    val unsplashImageById: LiveData<Resources<UnsplashPhoto>> get() = _unplashImageById

    fun getUnsplashImageByID(id: String) {

        try {

            viewModelScope.launch {

                _unplashImageById.postValue(Resources.Loading())

                repository.getImageByID(id).let {

                    if (it.isSuccessful) {

                        _unplashImageById.postValue(Resources.Success(it.body()))
                    } else {

                        _unplashImageById.postValue(Resources.Error(message = it.message()))
                    }
                }
            }
        } catch (e: Exception) {

            _unplashImageById.postValue(Resources.Error(e.message))

            e.printStackTrace()
        }
    }

    //for getting random images

    private val _unsplashRandomImage = MutableLiveData<Resources<ArrayList<UnsplashPhoto>>>()
    val unsplashRandomImage: LiveData<Resources<ArrayList<UnsplashPhoto>>> get() = _unsplashRandomImage

    fun getRandomUnsplashImage(count: Int = 30) {

        try {

            viewModelScope.launch {

                repository.getRandomImages(count).let {

                    _unsplashRandomImage.postValue(Resources.Loading())

                    if (it.isSuccessful) {

                        _unsplashRandomImage.postValue(Resources.Success(it.body()))
                    } else {

                        _unsplashRandomImage.postValue(Resources.Error(it.message()))
                    }
                }
            }
        } catch (e: Exception) {

            _unsplashRandomImage.postValue(Resources.Error(e.message))
            e.printStackTrace()
        }
    }
}