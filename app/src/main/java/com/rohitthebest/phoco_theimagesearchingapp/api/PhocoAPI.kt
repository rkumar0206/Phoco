package com.rohitthebest.phoco_theimagesearchingapp.api

import com.google.gson.annotations.SerializedName
import com.rohitthebest.phoco_theimagesearchingapp.data.phocoData.PhocoUser
import com.rohitthebest.phoco_theimagesearchingapp.data.phocoData.SignUp
import retrofit2.Response
import retrofit2.http.*

data class UserResponse(
        val pk: Int,
        val username: String,
        val email: String,
        val first_name: String,
        val last_name: String?
)

data class Tokens(
        @SerializedName("refresh") val refreshToken: String,
        @SerializedName("access") val accessToken: String
)

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

    //------------------------------ refresh tokens --------------------------------------

    @FormUrlEncoded
    @POST("/auth/login/refresh/")
    suspend fun getNewTokens(
            @Field("refresh") refreshToken: String
    ): Response<Tokens>
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

}