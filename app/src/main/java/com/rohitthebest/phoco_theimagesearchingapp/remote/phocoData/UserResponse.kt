package com.rohitthebest.phoco_theimagesearchingapp.remote.phocoData

data class UserResponse(
    val pk: Int,
    val username: String,
    val email: String,
    val first_name: String,
    val last_name: String?
)