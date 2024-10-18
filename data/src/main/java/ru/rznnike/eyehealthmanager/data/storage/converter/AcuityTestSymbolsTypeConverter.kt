package ru.rznnike.eyehealthmanager.data.storage.converter

import io.objectbox.converter.PropertyConverter
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestSymbolsType

class AcuityTestSymbolsTypeConverter : PropertyConverter<AcuityTestSymbolsType, Int> {
    override fun convertToDatabaseValue(entityProperty: AcuityTestSymbolsType?) =
        (entityProperty ?: AcuityTestSymbolsType.LETTERS_RU).id

    override fun convertToEntityProperty(databaseValue: Int?) =
        AcuityTestSymbolsType[databaseValue]
}