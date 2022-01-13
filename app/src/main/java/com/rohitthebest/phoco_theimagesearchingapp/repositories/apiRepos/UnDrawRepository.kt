package com.rohitthebest.phoco_theimagesearchingapp.repositories.apiRepos

import com.rohitthebest.phoco_theimagesearchingapp.api.UnDrawAPI
import com.rohitthebest.phoco_theimagesearchingapp.api.UnDrawRequestModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnDrawRepository @Inject constructor(
    private val unDrawAPI: UnDrawAPI
) {

    suspend fun getUndrawImages(query: UnDrawRequestModel) =
        unDrawAPI.searchUnDrawImages(query = query)
}