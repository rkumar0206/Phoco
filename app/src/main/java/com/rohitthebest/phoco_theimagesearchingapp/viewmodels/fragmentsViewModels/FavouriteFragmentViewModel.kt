package com.rohitthebest.phoco_theimagesearchingapp.viewmodels.fragmentsViewModels

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class FavouriteFragmentViewModel(
    private val state: SavedStateHandle
) : ViewModel() {

    companion object {

        private val FAVOURITE_RV_STATE_KEY = "zscnakckab"
    }

    fun saveFavouriteRVState(rvState: Parcelable?) {

        state.set(FAVOURITE_RV_STATE_KEY, rvState)
    }

    private val _favouriteRVState: MutableLiveData<Parcelable> =
        state.getLiveData(FAVOURITE_RV_STATE_KEY, null)

    val favouriteRVState: LiveData<Parcelable> get() = _favouriteRVState

}