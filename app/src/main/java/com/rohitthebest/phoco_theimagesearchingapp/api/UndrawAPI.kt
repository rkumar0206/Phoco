package com.rohitthebest.phoco_theimagesearchingapp.api

import com.rohitthebest.phoco_theimagesearchingapp.remote.undrawData.UndrawResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class UndrawRequestModel(
    var query: String
)

interface UndrawAPI {

    @POST("/search")
    suspend fun searchUndrawImages(
        @Body query: UndrawRequestModel
    ): Response<UndrawResponse>
}