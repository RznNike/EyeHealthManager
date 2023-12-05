package ru.rznnike.eyehealthmanager.data.storage.converter

import io.objectbox.converter.PropertyConverter
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuityTestSymbolsType

class AcuityTestSymbolsTypeConverter : PropertyConverter<AcuityTestSymbolsType, Int> {
    override fun convertToDatabaseValue(entityProperty: AcuityTestSymbolsType?): Int {
        return (entityProperty ?: AcuityTestSymbolsType.LETTERS_RU).id
    }

    override fun convertToEntityProperty(databaseValue: Int?): AcuityTestSymbolsType {
        return AcuityTestSymbolsType[databaseValue]
    }
}