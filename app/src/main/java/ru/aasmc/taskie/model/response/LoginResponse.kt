package ru.aasmc.taskie.model.response

import com.squareup.moshi.Json

data class LoginResponse(
    @field:Json(name = "token")
    val token: String? = ""
)