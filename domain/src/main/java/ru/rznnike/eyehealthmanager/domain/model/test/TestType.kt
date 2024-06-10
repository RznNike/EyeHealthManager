package ru.rznnike.eyehealthmanager.domain.model.test

import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.astigmatism.AstigmatismTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.colorperception.ColorPerceptionTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.contrast.ContrastTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.daltonism.DaltonismTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.nearfar.NearFarTestResult

enum class TestType(
    val id: Int,
    val exportHeader: String
) {
    ACUITY(
        id = 1,
        exportHeader = AcuityTestResult.EXPORT_HEADER
    ),
    ASTIGMATISM(
        id = 3,
        exportHeader = AstigmatismTestResult.EXPORT_HEADER
    ),
    NEAR_FAR(
        id = 4,
        exportHeader = NearFarTestResult.EXPORT_HEADER
    ),
    COLOR_PERCEPTION(
        id = 5,
        exportHeader = ColorPerceptionTestResult.EXPORT_HEADER
    ),
    DALTONISM(
        id = 6,
        exportHeader = DaltonismTestResult.EXPORT_HEADER
    ),
    CONTRAST(
        id = 7,
        exportHeader = ContrastTestResult.EXPORT_HEADER
    );

    companion object {
        operator fun get(id: Int?) = entries.find { it.id == id } ?: ACUITY
    }
}