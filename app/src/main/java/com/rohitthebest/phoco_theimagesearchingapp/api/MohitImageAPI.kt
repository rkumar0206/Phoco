package com.rohitthebest.phoco_theimagesearchingapp.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MohitImageAPI {

    @GET("/api/getimage/{query}")
    suspend fun searchImage(

            @Path("query") query: String
    ): Response<WebResponse>
}