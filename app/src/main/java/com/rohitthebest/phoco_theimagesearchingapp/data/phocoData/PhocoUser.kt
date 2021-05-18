package com.rohitthebest.phoco_theimagesearchingapp.data.phocoData

data class PhocoUser(
        val pk: Int,
        val created: String,
        val updated: String,
        val name: String,
        val phoco_user_id: String,
        val user: Int,
        val user_image_url: String,
        val user_profile_url: String,
        val username: String,
        val followers: Int,
        val following: Int
)