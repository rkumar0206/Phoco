package com.rohitthebest.phoco_theimagesearchingapp.api

import com.rohitthebest.phoco_theimagesearchingapp.remote.phocoData.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface PhocoAPI {

    //----------------------------- Sign up -----------------------------------

    @POST("/auth/signup/")
    suspend fun signUpUser(
            @Body signUp: SignUp
    ): Response<UserResponse>   // status 201 created

    //-------------------------------------------------------------------------------


    //------------------------------ Log In ---------------------------------------

    @FormUrlEncoded
    @POST("/auth/login/")
    suspend fun loginUser(
            @Field("username") username: String,
            @Field("password") password: String
    ): Response<Tokens>
    //-------------------------------------------------------------------------------

    //------------------------------ tokens --------------------------------------

    //get new tokens using the refresh tokens
    @FormUrlEncoded
    @POST("/auth/login/refresh/")
    suspend fun getNewTokens(
        @Field("refresh") refreshToken: String
    ): Response<Tokens>

    //verify token
    @FormUrlEncoded
    @POST("/auth/token_verify/")
    suspend fun verifyToken(
        @Field("token") token: String
    ): Response<Void>

    //-------------------------------------------------------------------------------


    //------------------------------- User Details -----------------------------------

    // call when only primary key of the user is available
    @GET("/user_detail/{pk}/")
    suspend fun getPhocoUserByPrimaryKey(
        @Path("pk") primaryKey: Int,
            @Header("Authorization") accessToken: String
    ): Response<PhocoUser>


    // call at the time after the login and getting the access token
    @GET("/user_detail/{username}/")
    suspend fun getPhocoUserByUsername(
            @Path("username") username: String,
            @Header("Authorization") accessToken: String
    ): Response<PhocoUser>

    //-------------------------------------------------------------------------------


    //--------------------------- Update User details --------------------------------------

    @FormUrlEncoded
    @PUT("/auth/update_user_detail/{pk}/")
    suspend fun updateUser(
            @Path("pk") primaryKey: Int,
            @Field("username") username: String,
            @Field("email") email: String,
            @Field("first_name") firstName: String,
            @Field("last_name") lastName: String? = "",
            @Header("Authorization") accessToken: String
    ): Response<UserResponse>

    //-------------------------------------------------------------------------------


    //------------------------------ Change user password ---------------------------------

    @FormUrlEncoded
    @PUT("/auth/change_password/{pk}/")
    suspend fun changeUserPassword(
        @Path("pk") primaryKey: Int,
        @Header("Authorization") accessToken: String,
        @Field("old_password") oldPassword: String,
        @Field("password") newPassword: String,
        @Field("password2") confirmPassword: String
    ): Response<UserResponse>   // status 200OK

    //-------------------------------------------------------------------------------


    // reset password
    // logout


    //------------------------------- Follower / Following ----------------------------------

    @FormUrlEncoded
    @POST("/follow/")
    suspend fun followUser(
        @Header("Authorization") accessToken: String,
        @Field("follower_user_pk") follower_user_pk: Int,
        @Field("following_user_pk") following_user_pk: Int
    ): Response<Follow>


    @GET("/user_followers/{pk}/")
    suspend fun getUserFollowers(
        @Path("pk") userPrimaryKey: Int,
        @Header("Authorization") accessToken: String
    ): Response<List<PhocoUser>>


    @GET("/user_following/{pk}/")
    suspend fun getUserFollowing(
        @Path("pk") userPrimaryKey: Int,
        @Header("Authorization") accessToken: String
    ): Response<List<PhocoUser>>

    @DELETE("/unfollow_user/{follower}/{following}/")
    suspend fun unfollowUser(
        @Header("Authorization") accessToken: String,
        @Path("follower") follower_user_pk: Int,
        @Path("following") following_user_pk: Int
    ): Response<String?>   // testing required

    //-------------------------------------------------------------------------------


    //------------------------------- Image Related ----------------------------------

    @GET("/images/{user_pk}/")
    suspend fun getUserPhocoImages(
        @Header("Authorization") accessToken: String,
        @Path("user_pk") user_pk: Int,
        @Query("user_id") user_id: Int = user_pk,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Response<PhocoImageResponse>

    @GET("/images_liked/{user_pk}/")
    suspend fun getUserLikedImages(
        @Header("Authorization") accessToken: String,
        @Path("user_pk") user_pk: Int,
        @Query("user_id") user_id: Int = user_pk,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Response<PhocoImageResponse>

    @GET("/images_following/{user_pk}")
    suspend fun getImagesBasedOnUserFollowing(
        @Header("Authorization") accessToken: String,
        @Path("user_pk") user_pk: Int,
        @Query("user_id") user_id: Int = user_pk,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Response<PhocoImageResponse>

    @Multipart
    @POST("/images/")
    suspend fun postImage(
        @Header("Authorization") accessToken: String,
        @Part image: MultipartBody.Part,
        @Part("image_description") imageDescription: RequestBody,
        @Part("phoco_user") user: RequestBody
    ): Response<PhocoImageItem>

    @FormUrlEncoded
    @POST("/like/")
    suspend fun likeOrUnlikeImage(
        @Header("Authentication") accessToken: String,
        @Field("user") user_pk: Int,
        @Field("image") image_pk: Int
    ): Response<String>

    //-------------------------------------------------------------------------------


}