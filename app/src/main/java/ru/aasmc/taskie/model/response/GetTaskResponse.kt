package ru.aasmc.taskie.model.response

import kotlinx.serialization.Serializable
import ru.aasmc.taskie.model.Task

@Serializable
data class GetTasksResponse(
    val notes: List<Task> = mutableListOf()
)
