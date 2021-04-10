package com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.rohitthebest.phoco_theimagesearchingapp.data.Resources
import com.rohitthebest.phoco_theimagesearchingapp.data.pixabayData.PixabayPhoto
import com.rohitthebest.phoco_theimagesearchingapp.data.pixabayData.PixabayRepository
import kotlinx.coroutines.launch

class PixabayViewModel @ViewModelInject constructor(
        private val repository: PixabayRepository
) : ViewModel() {

    //for searching image
    private val currentQuery = MutableLiveData<String>()

    fun searchWithPixabay(searchQuery: String) {

        currentQuery.postValue(searchQuery)
    }

    val pixabaySearchResult = currentQuery.switchMap {

        repository.getSearchResultsFromPixabayAPI(it).asLiveData().cachedIn(viewModelScope)
    }

    private val _pixabayImageByID = MutableLiveData<Resources<PixabayPhoto>>()

    val pixabayImageById: LiveData<Resources<PixabayPhoto>> get() = _pixabayImageByID

    fun getPixabayImageById(id: Int) {

        try {

            viewModelScope.launch {

                _pixabayImageByID.postValue(Resources.Loading())

                val response = repository.getPhotoById(id)

                if (response.isSuccessful) {

                    _pixabayImageByID.postValue(Resources.Success(response.body()))

                } else {

                    _pixabayImageByID.postValue(Resources.Error(response.message()))
                }
            }
        } catch (e: Exception) {

            _pixabayImageByID.postValue(Resources.Error(e.message))

            e.printStackTrace()
        }
    }
}