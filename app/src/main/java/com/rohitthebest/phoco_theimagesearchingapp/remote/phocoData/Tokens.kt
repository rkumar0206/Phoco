package com.rohitthebest.phoco_theimagesearchingapp.remote.phocoData

import com.google.gson.annotations.SerializedName

data class Tokens(
    @SerializedName("refresh") val refreshToken: String,
    @SerializedName("access") val accessToken: String
)