package ru.rznnike.eyehealthmanager.domain.model.enums

enum class AnalysisType(
    val id: Int
) {
    ACUITY_ONLY(1),
    CONSOLIDATED_REPORT(1);

    companion object {
        operator fun get(id: Int?) = entries.find { it.id == id } ?: ACUITY_ONLY
    }
}