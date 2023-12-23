package ru.rznnike.eyehealthmanager.data.storage.converter

import io.objectbox.converter.PropertyConverter
import ru.rznnike.eyehealthmanager.domain.model.enums.DaltonismAnomalyType

class DaltonismAnomalyTypeConverter : PropertyConverter<DaltonismAnomalyType, Int> {
    override fun convertToDatabaseValue(entityProperty: DaltonismAnomalyType?) =
        (entityProperty ?: DaltonismAnomalyType.NONE).id

    override fun convertToEntityProperty(databaseValue: Int?) =
        DaltonismAnomalyType[databaseValue]
}