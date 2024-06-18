package ru.rznnike.eyehealthmanager.domain.model.test

enum class TestEyesType(
    val id: Int
) {
    BOTH(1),
    LEFT(2),
    RIGHT(3);

    companion object {
        operator fun get(id: Int?) = entries.find { it.id == id } ?: BOTH

        operator fun get(name: String?) = entries.find { it.toString() == name }
    }
}