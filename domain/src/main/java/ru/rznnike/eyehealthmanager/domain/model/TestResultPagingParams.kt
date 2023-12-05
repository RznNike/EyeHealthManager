package ru.rznnike.eyehealthmanager.domain.model

data class TestResultPagingParams(
    val limit: Int,
    val offset: Int,
    val filterParams: TestResultFilterParams?
)