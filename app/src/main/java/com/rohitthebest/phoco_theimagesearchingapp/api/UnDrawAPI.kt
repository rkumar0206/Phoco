package com.rohitthebest.phoco_theimagesearchingapp.api

import com.rohitthebest.phoco_theimagesearchingapp.remote.undrawData.UnDrawResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class UnDrawRequestModel(
    var query: String
)

interface UnDrawAPI {

    @POST("/search")
    suspend fun searchUnDrawImages(
        @Body query: UnDrawRequestModel
    ): Response<UnDrawResponse>
}