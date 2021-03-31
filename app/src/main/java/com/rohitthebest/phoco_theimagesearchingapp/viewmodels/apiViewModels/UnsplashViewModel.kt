package com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.rohitthebest.phoco_theimagesearchingapp.data.unsplashData.UnsplashRepository

class UnsplashViewModel @ViewModelInject constructor(
    private val repository: UnsplashRepository
) : ViewModel() {


}