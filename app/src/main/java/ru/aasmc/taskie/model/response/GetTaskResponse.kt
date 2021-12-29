package ru.aasmc.taskie.model.response

import com.squareup.moshi.Json
import ru.aasmc.taskie.model.Task

data class GetTasksResponse(
    @field:Json(name = "notes")
    val notes: List<Task> = mutableListOf()
)
