package com.rohitthebest.phoco_theimagesearchingapp.viewmodels.fragmentsViewModels

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class HomeFragmentViewModel(
    private val state: SavedStateHandle
) : ViewModel() {

    companion object {

        private val HOME_FRAGMENT_SAVE_KEY = "nfjvnkjbskb"
    }

    fun saveRVState(rvState: Parcelable?) {

        state.set(HOME_FRAGMENT_SAVE_KEY, rvState)
    }

    private val _rVState: MutableLiveData<Parcelable> =
        state.getLiveData(HOME_FRAGMENT_SAVE_KEY, null)

    val rVState: LiveData<Parcelable> get() = _rVState


}