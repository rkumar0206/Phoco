package com.rohitthebest.phoco_theimagesearchingapp.utils

import android.app.Activity
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rohitthebest.phoco_theimagesearchingapp.Constants
import com.rohitthebest.phoco_theimagesearchingapp.data.AuthToken


fun saveAuthTokenInSharedPreferences(
    activity: Activity,
    authToken: AuthToken
) {

    val sharedPreference =
        activity.getSharedPreferences(
            Constants.AUTH_TOKEN_SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )

    val edit = sharedPreference.edit()

    val gson = Gson()

    val authTokenJson = gson.toJson(authToken)

    edit.putString(Constants.AUTH_TOKEN_SHARED_PREFERENCE_KEY, authTokenJson)

    edit.apply()
}

fun getSavedAuthToken(activity: Activity): AuthToken? {

    val sharedPreference =
        activity.getSharedPreferences(
            Constants.AUTH_TOKEN_SHARED_PREFERENCE_NAME,
            Context.MODE_PRIVATE
        )

    val authTokenJsonString =
        sharedPreference.getString(Constants.AUTH_TOKEN_SHARED_PREFERENCE_KEY, "")

    return if (authTokenJsonString != null && authTokenJsonString.isValidString()) {

        val gson = Gson()

        val type = object : TypeToken<AuthToken>() {}.type

        gson.fromJson(authTokenJsonString, type)

    } else {

        null
    }
}