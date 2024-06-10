package ru.rznnike.eyehealthmanager.domain.model.journal

data class TestResultPagingParameters(
    val limit: Int,
    val offset: Int,
    val filter: TestResultFilter?
)