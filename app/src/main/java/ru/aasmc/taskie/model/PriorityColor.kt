package ru.aasmc.taskie.model

import ru.aasmc.taskie.R

/**
 * Describes the task priority levels, when coloring the task items.
 */
enum class PriorityColor {

    LOW, MEDIUM, HIGH;

    fun getColor() = when (this) {
        LOW -> R.color.priorityLow
        MEDIUM -> R.color.priorityMedium
        HIGH -> R.color.priorityHigh
    }
}