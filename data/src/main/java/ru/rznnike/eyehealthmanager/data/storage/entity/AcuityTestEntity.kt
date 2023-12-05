package ru.rznnike.eyehealthmanager.data.storage.entity

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import ru.rznnike.eyehealthmanager.data.storage.converter.AcuityTestSymbolsTypeConverter
import ru.rznnike.eyehealthmanager.data.storage.converter.DayPartConverter
import ru.rznnike.eyehealthmanager.data.storage.converter.TestEyesTypeConverter
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.enums.DayPart
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType

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
)