package ru.aasmc.taskie.model.response

import ru.aasmc.taskie.model.Task

data class GetTasksResponse(val notes: List<Task> = mutableListOf())
