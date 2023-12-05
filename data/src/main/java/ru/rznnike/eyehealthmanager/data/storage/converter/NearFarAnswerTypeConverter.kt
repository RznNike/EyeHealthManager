package ru.rznnike.eyehealthmanager.data.storage.converter

import io.objectbox.converter.PropertyConverter
import ru.rznnike.eyehealthmanager.domain.model.enums.NearFarAnswerType

class NearFarAnswerTypeConverter : PropertyConverter<NearFarAnswerType, Int> {
    override fun convertToDatabaseValue(entityProperty: NearFarAnswerType?): Int {
        return (entityProperty ?: NearFarAnswerType.EQUAL).id
    }

    override fun convertToEntityProperty(databaseValue: Int?): NearFarAnswerType {
        return NearFarAnswerType[databaseValue]
    }
}