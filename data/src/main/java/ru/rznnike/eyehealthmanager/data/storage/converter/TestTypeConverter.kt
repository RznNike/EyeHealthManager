package ru.rznnike.eyehealthmanager.data.storage.converter

import io.objectbox.converter.PropertyConverter
import ru.rznnike.eyehealthmanager.domain.model.test.TestType

class TestTypeConverter : PropertyConverter<TestType, Int> {
    override fun convertToDatabaseValue(entityProperty: TestType?) =
        (entityProperty ?: TestType.ACUITY).id

    override fun convertToEntityProperty(databaseValue: Int?) =
        TestType[databaseValue]
}