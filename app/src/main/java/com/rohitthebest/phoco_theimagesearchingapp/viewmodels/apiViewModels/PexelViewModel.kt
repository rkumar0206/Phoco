package com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.rohitthebest.phoco_theimagesearchingapp.data.pexelsData.PexelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PexelViewModel @Inject constructor(

    private val pexelRepository: PexelRepository
) : ViewModel() {

    private val currentQuery = MutableLiveData<String>()

    val pexelSearchResult = currentQuery.switchMap {

        pexelRepository.getSearchResultFromThePexelApi(it).asLiveData().cachedIn(viewModelScope)
    }

    fun searchImage(searchQuery: String) {

        currentQuery.value = searchQuery
    }
}