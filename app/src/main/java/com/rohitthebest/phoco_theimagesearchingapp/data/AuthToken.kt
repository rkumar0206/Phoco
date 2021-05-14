package com.rohitthebest.phoco_theimagesearchingapp.data

data class AuthToken(
    var refreshToken: String,
    var accessToken: String,
    var dateWhenTokenReceived: Long
)
