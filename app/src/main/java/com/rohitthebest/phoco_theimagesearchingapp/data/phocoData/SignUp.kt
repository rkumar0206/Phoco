package com.rohitthebest.phoco_theimagesearchingapp.data.phocoData

import com.google.gson.annotations.SerializedName

data class SignUp(
        var username: String,
        var password: String,
        @SerializedName("password2") var confirmPassword: String,
        var email: String,
        var first_name: String,
        var last_name: String = ""
) {

    constructor() : this(
            "",
            "",
            "",
            "",
            "",
            ""
    )
}
