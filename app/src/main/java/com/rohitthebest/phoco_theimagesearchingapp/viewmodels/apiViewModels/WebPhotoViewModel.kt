package com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitthebest.phoco_theimagesearchingapp.api.WebResponse
import com.rohitthebest.phoco_theimagesearchingapp.remote.Resources
import com.rohitthebest.phoco_theimagesearchingapp.remote.mohitImagApiData.WebPhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebPhotoViewModel @Inject constructor(
        private val webImageRepository: WebPhotoRepository
) : ViewModel() {

    private val _webImage = MutableLiveData<Resources<WebResponse>>()

    val webImage: LiveData<Resources<WebResponse>> get() = _webImage

    fun searchImage(searchQuery: String) {

        try {

            viewModelScope.launch {

                _webImage.postValue(Resources.Loading())

                webImageRepository.searchImage(searchQuery).let {

                    if (it.isSuccessful) {

                        _webImage.postValue(Resources.Success(it.body()))
                    } else {

                        _webImage.postValue(Resources.Error(it.message()))
                    }
                }
            }

        } catch (e: Exception) {

            e.printStackTrace()

            _webImage.postValue(Resources.Error(e.message))
        }
    }
}