package com.rohitthebest.phoco_theimagesearchingapp.remote.phocoData

import com.google.gson.annotations.SerializedName

data class Follow(
    val pk: Int,
    @SerializedName("follower_user_pk") val followUserPK: Int,
    @SerializedName("following_user_pk") val followingUserPk: Int
)