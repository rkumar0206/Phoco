package com.rohitthebest.phoco_theimagesearchingapp.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rohitthebest.phoco_theimagesearchingapp.Constants.AUTH_TOKEN_SHARED_PREFERENCE_KEY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.AUTH_TOKEN_SHARED_PREFERENCE_NAME
import com.rohitthebest.phoco_theimagesearchingapp.Constants.PHOCO_USER_DATA_SHARED_PREFERENCE_KEY
import com.rohitthebest.phoco_theimagesearchingapp.Constants.PHOCO_USER_DATA_SHARED_PREFERENCE_NAME
import com.rohitthebest.phoco_theimagesearchingapp.remote.AuthToken
import com.rohitthebest.phoco_theimagesearchingapp.remote.phocoData.PhocoUser

private const val TAG = "SharedPreferencesData"

val gson = Gson()

fun saveAuthTokenInSharedPreferences(
    activity: Activity,
    authToken: AuthToken
) {

    val sharedPreference =
        activity.getSharedPreferences(
            AUTH_TOKEN_SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )

    val edit = sharedPreference.edit()

    val authTokenJson = gson.toJson(authToken)

    edit.putString(AUTH_TOKEN_SHARED_PREFERENCE_KEY, authTokenJson)

    edit.apply()
}

fun getSavedAuthToken(activity: Activity): AuthToken? {

    val sharedPreference =
        activity.getSharedPreferences(
            AUTH_TOKEN_SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )

    val authTokenJsonString =
        sharedPreference.getString(AUTH_TOKEN_SHARED_PREFERENCE_KEY, "")

    return if (authTokenJsonString != null && authTokenJsonString.isValidString()) {

        val type = object : TypeToken<AuthToken>() {}.type

        gson.fromJson(authTokenJsonString, type)

    } else {

        null
    }
}

fun deleteAuthTokensFromSharedPreference(activity: Activity) {

    val sharedPreference =
        activity.getSharedPreferences(
            AUTH_TOKEN_SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )

    val edit = sharedPreference.edit()

    edit.clear()

    edit.apply()

    Log.d(TAG, "deleteAuthTokensFromSharedPreference: deleted")
}

fun saveUserProfileSharedPreferences(
    activity: Activity,
    phocoUser: PhocoUser
) {

    val sharedPreference = activity.getSharedPreferences(
        PHOCO_USER_DATA_SHARED_PREFERENCE_NAME,
        Context.MODE_PRIVATE
    )

    val edit = sharedPreference.edit()

    val phocoUserJsonString = gson.toJson(phocoUser)

    edit.putString(PHOCO_USER_DATA_SHARED_PREFERENCE_KEY, phocoUserJsonString)

    edit.apply()
}

fun getUserProfileData(activity: Activity): PhocoUser? {

    val sharedPreference = activity.getSharedPreferences(
        PHOCO_USER_DATA_SHARED_PREFERENCE_NAME,
        Context.MODE_PRIVATE
    )

    val phocoUserJsonString = sharedPreference.getString(PHOCO_USER_DATA_SHARED_PREFERENCE_KEY, "")

    return if (phocoUserJsonString != null && phocoUserJsonString.isValidString()) {

        val type = object : TypeToken<PhocoUser>() {}.type

        gson.fromJson(phocoUserJsonString, type)
    } else {

        null
    }
}

fun deleteUserProfileDataFromSharedPreference(activity: Activity) {

    val sharedPreference =
        activity.getSharedPreferences(
            PHOCO_USER_DATA_SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )

    val edit = sharedPreference.edit()

    edit.clear()

    edit.apply()

    Log.d(TAG, "deleteUserProfileDataFromSharedPreference: deleted")
}