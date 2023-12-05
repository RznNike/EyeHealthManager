package ru.rznnike.eyehealthmanager.domain.model.enums

enum class Language(val tag: String, val localizedName: String) {
    RU("ru", "Русский"),
    EN("en", "English");

    companion object {
        operator fun get(tag: String) = values().find { it.tag == tag } ?: EN
    }
}