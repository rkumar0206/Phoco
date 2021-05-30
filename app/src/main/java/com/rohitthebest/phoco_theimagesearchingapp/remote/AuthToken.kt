package com.rohitthebest.phoco_theimagesearchingapp.remote

data class AuthToken(
    var refreshToken: String,
    var accessToken: String,
    var dateWhenTokenReceived: Long
)
