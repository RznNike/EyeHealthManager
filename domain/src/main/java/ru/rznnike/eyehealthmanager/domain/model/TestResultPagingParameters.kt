package ru.rznnike.eyehealthmanager.domain.model

data class TestResultPagingParameters(
    val limit: Int,
    val offset: Int,
    val filter: TestResultFilter?
)