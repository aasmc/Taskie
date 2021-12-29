package ru.aasmc.taskie.model.response

import com.squareup.moshi.Json

class CompleteNoteResponse(
    @field:Json(name = "message")
    val message: String?
)