package ru.rznnike.eyehealthmanager.data.storage.converter

import io.objectbox.converter.PropertyConverter
import ru.rznnike.eyehealthmanager.domain.model.enums.DaltonismAnomalyType

class DaltonismAnomalyTypeConverter : PropertyConverter<DaltonismAnomalyType, Int> {
    override fun convertToDatabaseValue(entityProperty: DaltonismAnomalyType?): Int {
        return (entityProperty ?: DaltonismAnomalyType.NONE).id
    }

    override fun convertToEntityProperty(databaseValue: Int?): DaltonismAnomalyType {
        return DaltonismAnomalyType[databaseValue]
    }
}