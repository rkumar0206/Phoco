package com.rohitthebest.phoco_theimagesearchingapp.api

import com.google.gson.annotations.SerializedName
import com.rohitthebest.phoco_theimagesearchingapp.data.phocoData.PhocoUser
import com.rohitthebest.phoco_theimagesearchingapp.data.phocoData.SignUp
import retrofit2.Response
import retrofit2.http.*

data class User(
        val username: String,
        val email: String,
        val first_name: String,
        val second_name: String?
)

data class Tokens(
        @SerializedName("refresh") val refreshToken: String,
        @SerializedName("access") val accessToken: String
)

interface PhocoAPI {

    //----------------------------- Sign up -----------------------------------

    @POST("/signup/")
    suspend fun signUpUser(
            @Body signUp: SignUp
    ): Response<User>   // status 201 created

    //-------------------------------------------------------------------------------


    //------------------------------ Log In ---------------------------------------

    @FormUrlEncoded
    @POST("/login/")
    suspend fun loginUser(
            @Field("username") username: String,
            @Field("password") password: String
    ): Response<Tokens>
    //-------------------------------------------------------------------------------

    //------------------------------ refresh tokens --------------------------------------

    @FormUrlEncoded
    @POST("/login/refresh/")
    suspend fun getRefreshToken(
            @Field("refresh") refreshToken: String
    ): Response<Tokens>
    //-------------------------------------------------------------------------------


    //------------------------------- User Details -----------------------------------

    @GET("/user_detail/{pk}/")
    suspend fun getPhocoUser(
            @Path("pk") primaryKey: Int,
            @Header("Authorization") accessToken: String
    ): Response<PhocoUser>

    //-------------------------------------------------------------------------------


    //--------------------------- Update User details --------------------------------------

    @FormUrlEncoded
    @PUT("/update_user_detail/{pk}/")
    suspend fun updateUser(
            @Path("pk") primaryKey: Int,
            @Field("username") username: String,
            @Field("email") email: String,
            @Field("first_name") firstName: String,
            @Field("last_name") lastName: String = "",
            @Header("Authorization") accessToken: String
    ): Response<User>

    //-------------------------------------------------------------------------------


    //------------------------------ Change user password ---------------------------------

    @FormUrlEncoded
    @PUT("/change_password/{pk}/")
    suspend fun changeUserPassword(
            @Path("pk") primaryKey: String,
            @Header("Authorization") accessToken: String,
            @Field("old_password") oldPassword: String,
            @Field("password") newPassword: String,
            @Field("password2") confirmPassword: String
    ): Response<User>   // status 200OK

    //-------------------------------------------------------------------------------


    // reset password
    // logout

}