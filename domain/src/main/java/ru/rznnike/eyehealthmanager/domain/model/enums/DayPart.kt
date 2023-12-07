package ru.rznnike.eyehealthmanager.domain.model.enums

enum class DayPart(
    val id: Int
) {
    BEGINNING(1),
    MIDDLE(2),
    END(3);

    companion object {
        operator fun get(id: Int?) = entries.find { it.id == id } ?: MIDDLE

        operator fun get(name: String?) = entries.find { it.toString() == name }
    }
}