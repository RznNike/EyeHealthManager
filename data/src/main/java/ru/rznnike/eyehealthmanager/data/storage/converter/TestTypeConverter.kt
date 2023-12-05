package ru.rznnike.eyehealthmanager.data.storage.converter

import io.objectbox.converter.PropertyConverter
import ru.rznnike.eyehealthmanager.domain.model.enums.TestType

class TestTypeConverter : PropertyConverter<TestType, Int> {
    override fun convertToDatabaseValue(entityProperty: TestType?): Int {
        return (entityProperty ?: TestType.ACUITY).id
    }

    override fun convertToEntityProperty(databaseValue: Int?): TestType {
        return TestType[databaseValue]
    }
}