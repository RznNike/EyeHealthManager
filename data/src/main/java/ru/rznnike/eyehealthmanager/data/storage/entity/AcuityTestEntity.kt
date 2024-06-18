package ru.rznnike.eyehealthmanager.data.storage.entity

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import ru.rznnike.eyehealthmanager.data.storage.converter.AcuityTestSymbolsTypeConverter
import ru.rznnike.eyehealthmanager.data.storage.converter.DayPartConverter
import ru.rznnike.eyehealthmanager.data.storage.converter.TestEyesTypeConverter
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.common.DayPart
import ru.rznnike.eyehealthmanager.domain.model.test.TestEyesType

@Entity
data class AcuityTestEntity(
    @Id var id: Long = 0,
    @Convert(converter = AcuityTestSymbolsTypeConverter::class, dbType = Int::class)
    val symbolsType: AcuityTestSymbolsType = AcuityTestSymbolsType.LETTERS_RU,
    @Convert(converter = TestEyesTypeConverter::class, dbType = Int::class)
    val testEyesType: TestEyesType = TestEyesType.BOTH,
    @Convert(converter = DayPartConverter::class, dbType = Int::class)
    val dayPart: DayPart = DayPart.MIDDLE,
    val resultLeftEye: Int? = null,
    val resultRightEye: Int? = null,
    val measuredByDoctor: Boolean = false
) {
    fun toAcuityTestResult(parentEntity: TestEntity) = AcuityTestResult(
        id = parentEntity.id,
        timestamp = parentEntity.timestamp,
        symbolsType = symbolsType,
        testEyesType = testEyesType,
        dayPart = dayPart,
        resultLeftEye = resultLeftEye,
        resultRightEye = resultRightEye,
        measuredByDoctor = measuredByDoctor
    )
}

fun AcuityTestResult.toAcuityTestEntity() = AcuityTestEntity(
    symbolsType = symbolsType,
    testEyesType = testEyesType,
    dayPart = dayPart,
    resultLeftEye = resultLeftEye,
    resultRightEye = resultRightEye,
    measuredByDoctor = measuredByDoctor
)