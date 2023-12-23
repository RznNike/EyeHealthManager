package ru.rznnike.eyehealthmanager.data.storage.converter

import io.objectbox.converter.PropertyConverter
import ru.rznnike.eyehealthmanager.domain.model.enums.AstigmatismAnswerType

class AstigmatismAnswerTypeConverter : PropertyConverter<AstigmatismAnswerType, Int> {
    override fun convertToDatabaseValue(entityProperty: AstigmatismAnswerType?) =
        (entityProperty ?: AstigmatismAnswerType.OK).id

    override fun convertToEntityProperty(databaseValue: Int?) =
        AstigmatismAnswerType[databaseValue]
}