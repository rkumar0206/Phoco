package com.rohitthebest.phoco_theimagesearchingapp.data.phocoData

import com.google.gson.annotations.SerializedName

data class Tokens(
    @SerializedName("refresh") val refreshToken: String,
    @SerializedName("access") val accessToken: String
)