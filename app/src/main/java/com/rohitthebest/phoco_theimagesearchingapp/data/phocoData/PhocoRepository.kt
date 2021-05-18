package com.rohitthebest.phoco_theimagesearchingapp.data.phocoData

import com.rohitthebest.phoco_theimagesearchingapp.api.PhocoAPI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhocoRepository @Inject constructor(
        private val phocoAPI: PhocoAPI
) {

    suspend fun signUp(signUp: SignUp) = phocoAPI.signUpUser(signUp)

    suspend fun loin(username: String, password: String) = phocoAPI.loginUser(
            username, password
    )

    suspend fun getNewTokens(refreshToken: String) = phocoAPI.getNewTokens(refreshToken)

    suspend fun getPhocoUserByPrimaryKey(primaryKey: Int, accessToken: String) =
            phocoAPI.getPhocoUserByPrimaryKey(primaryKey, accessToken)

    suspend fun getPhocoUserByUsername(username: String, accessToken: String) =
            phocoAPI.getPhocoUserByUsername(username, accessToken)

    suspend fun updateUserDetails(userResponse: UserResponse, accessToken: String) =
        phocoAPI.updateUser(
            userResponse.pk,
            userResponse.username,
            userResponse.email,
            userResponse.first_name,
            userResponse.last_name,
            accessToken
        )

    suspend fun changeUserPassword(
        primaryKey: Int,
        accessToken: String,
        oldPassword: String,
        password: String
    ) =
        phocoAPI.changeUserPassword(primaryKey, accessToken, oldPassword, password, password)

    suspend fun followUser(accessToken: String, follower_user_pk: Int, following_user_pk: Int) =
        phocoAPI.followUser(accessToken, follower_user_pk, following_user_pk)

    suspend fun getUserFollowers(accessToken: String, userPk: Int) =
        phocoAPI.getUserFollowers(userPk, accessToken)

    suspend fun getUserFollowing(accessToken: String, userPk: Int) =
        phocoAPI.getUserFollowing(userPk, accessToken)

    suspend fun unfollowUser(accessToken: String, follower_user_pk: Int, following_user_pk: Int) =
        phocoAPI.unfollowUser(accessToken, follower_user_pk, following_user_pk)
}