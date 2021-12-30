package ru.aasmc.taskie.model.response

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String? = ""
)