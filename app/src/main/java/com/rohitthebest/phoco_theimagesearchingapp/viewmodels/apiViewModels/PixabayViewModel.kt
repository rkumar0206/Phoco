package com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.rohitthebest.phoco_theimagesearchingapp.remote.pixabayData.PixabayPhoto
import com.rohitthebest.phoco_theimagesearchingapp.remote.pixabayData.PixabayRepository
import com.rohitthebest.phoco_theimagesearchingapp.utils.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PixabayViewModel @Inject constructor(
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