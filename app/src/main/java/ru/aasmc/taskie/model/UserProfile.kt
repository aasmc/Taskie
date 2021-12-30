package ru.aasmc.taskie.model

import kotlinx.serialization.Serializable

/**
 * Full user data.
 */
class UserProfile(
    val email: String,
    val name: String,
    val numberOfNotes: Int
)