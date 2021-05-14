package com.rohitthebest.phoco_theimagesearchingapp.data.phocoData

import com.rohitthebest.phoco_theimagesearchingapp.api.PhocoAPI
import com.rohitthebest.phoco_theimagesearchingapp.api.UserResponse
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

    suspend fun changeUserPassword(primaryKey: Int, accessToken: String, oldPassword: String, password: String) =
            phocoAPI.changeUserPassword(primaryKey, accessToken, oldPassword, password, password)
}