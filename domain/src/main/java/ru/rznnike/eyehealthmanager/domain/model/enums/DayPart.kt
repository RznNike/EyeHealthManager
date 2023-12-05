package ru.rznnike.eyehealthmanager.domain.model.enums

enum class DayPart(
    val id: Int
) {
    BEGINNING(1),
    MIDDLE(2),
    END(3);

    companion object {
        operator fun get(id: Int?) = values().find { it.id == id } ?: MIDDLE

        fun parseName(string: String?) = values().find { it.toString() == string }
    }
}