package com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitthebest.phoco_theimagesearchingapp.api.UnDrawRequestModel
import com.rohitthebest.phoco_theimagesearchingapp.remote.undrawData.UnDrawResponse
import com.rohitthebest.phoco_theimagesearchingapp.repositories.apiRepos.UnDrawRepository
import com.rohitthebest.phoco_theimagesearchingapp.utils.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnDrawViewModel @Inject constructor(
    private val unDrawRepository: UnDrawRepository
) : ViewModel() {

    private val _unDrawImage = MutableLiveData<Resources<UnDrawResponse>>()

    val unDrawImage: LiveData<Resources<UnDrawResponse>> get() = _unDrawImage

    fun searchImage(query: String) {

        try {

            viewModelScope.launch {

                _unDrawImage.postValue(Resources.Loading())

                unDrawRepository.getUndrawImages(UnDrawRequestModel(query)).let { unDrawResponse ->

                    if (unDrawResponse.isSuccessful) {

                        _unDrawImage.postValue(Resources.Success(unDrawResponse.body()))
                    } else {

                        _unDrawImage.postValue(Resources.Error(unDrawResponse.message()))
                    }

                }
            }

        } catch (e: Exception) {

            e.printStackTrace()
            _unDrawImage.postValue(Resources.Error(e.message))
        }
    }

}