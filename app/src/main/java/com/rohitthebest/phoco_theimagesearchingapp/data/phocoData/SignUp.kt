package com.rohitthebest.phoco_theimagesearchingapp.data.phocoData

data class SignUp(
        var username: String,
        var password: String,
        var password2: String,
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
