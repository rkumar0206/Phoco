package com.rohitthebest.phoco_theimagesearchingapp.repositories.apiRepos

import com.rohitthebest.phoco_theimagesearchingapp.api.MohitImageAPI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebPhotoRepository @Inject constructor(
        private val mohitImageAPI: MohitImageAPI
) {

    suspend fun searchImage(searchQuery: String) = mohitImageAPI.searchImage(searchQuery)
}