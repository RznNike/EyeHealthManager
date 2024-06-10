package ru.rznnike.eyehealthmanager.domain.model.test.acuity

enum class AcuityTestSymbolsType(
    val id: Int
) {
    LETTERS_RU(1),
    LETTERS_EN(2),
    SQUARE(3),
    TRIANGLE(4);

    companion object {
        operator fun get(id: Int?) = entries.find { it.id == id } ?: LETTERS_RU

        operator fun get(name: String?) = entries.find { it.toString() == name }
    }
}