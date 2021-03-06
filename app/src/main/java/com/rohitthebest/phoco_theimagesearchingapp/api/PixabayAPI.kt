package com.rohitthebest.phoco_theimagesearchingapp.api

import com.rohitthebest.phoco_theimagesearchingapp.BuildConfig
import com.rohitthebest.phoco_theimagesearchingapp.remote.pixabayData.PixabayPhoto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayAPI {

    //search photos
    @GET("/api/")
    suspend fun searchWithPixabay(

            @Query("key") key: String = BuildConfig.PIXABAY_API_KEY,
            @Query("q") searchQuery: String,
            @Query("page") page: Int,
            @Query("per_page") per_page: Int
    ): PixabayResponse

    //get photo by id
    @GET("/api/")
    suspend fun getPhotoById(

        @Query("key") key: String = BuildConfig.PIXABAY_API_KEY,
        @Query("id") id: Int
    ): Response<PixabayPhoto>
}