package com.rohitthebest.phoco_theimagesearchingapp.api

import com.rohitthebest.phoco_theimagesearchingapp.BuildConfig.UNSPLASH_CLIENT_ID
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface UnsplashAPI {

   /* //for getting the list of photos
    @Headers("Accept-Version: v1", "Authorization: Client-ID $UNSPLASH_CLIENT_ID")
    @GET("photos")
    suspend fun getPhotos(
            @Query("page") page: Int,
            @Query("per_page") perPage: Int,
            @Query("order_by") orderBy: String = "latest"
    ): UnsplashResponse

*/
    //Retrieving single photo using id
    @Headers("Accept-Version: v1", "Authorization: Client-ID $UNSPLASH_CLIENT_ID")
    @GET("photos/:id")
    suspend fun getPhotoByID(
            @Query("id") id: String
    ): UnsplashResponse


    //getting photos related to search query
    @Headers("Accept-Version: v1", "Authorization: Client-ID $UNSPLASH_CLIENT_ID")
    @GET("search/photos")
    suspend fun searchPhoto(
            @Query("query") query: String,
            @Query("page") page: Int,
            @Query("per_page") perPage: Int,
            @Query("order_by") orderBy: String = "latest"
    ): UnsplashResponse


    //getting random photos
    @Headers("Accept-Version: v1", "Authorization: Client-ID $UNSPLASH_CLIENT_ID")
    @GET("photos/random")
    suspend fun getRandomPhotos(
            @Query("count") count: Int = 30
    ): UnsplashResponse


}