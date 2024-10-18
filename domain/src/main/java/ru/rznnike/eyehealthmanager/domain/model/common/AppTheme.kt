package ru.rznnike.eyehealthmanager.domain.model.common

enum class AppTheme(
    val id: Int
) {
    LIGHT(1),
    DARK(2),
    SYSTEM(3);

    companion object {
        operator fun get(id: Int?) = entries.find { it.id == id } ?: SYSTEM
    }
}