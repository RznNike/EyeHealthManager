package ru.rznnike.eyehealthmanager.data.storage.converter

import io.objectbox.converter.PropertyConverter
import ru.rznnike.eyehealthmanager.domain.model.enums.DayPart

class DayPartConverter : PropertyConverter<DayPart, Int> {
    override fun convertToDatabaseValue(entityProperty: DayPart?) =
        (entityProperty ?: DayPart.MIDDLE).id

    override fun convertToEntityProperty(databaseValue: Int?) =
        DayPart[databaseValue]
}