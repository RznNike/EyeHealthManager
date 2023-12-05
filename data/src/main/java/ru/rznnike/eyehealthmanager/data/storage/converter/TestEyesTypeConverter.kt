package ru.rznnike.eyehealthmanager.data.storage.converter

import io.objectbox.converter.PropertyConverter
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType

class TestEyesTypeConverter : PropertyConverter<TestEyesType, Int> {
    override fun convertToDatabaseValue(entityProperty: TestEyesType?): Int {
        return (entityProperty ?: TestEyesType.BOTH).id
    }

    override fun convertToEntityProperty(databaseValue: Int?): TestEyesType {
        return TestEyesType[databaseValue]
    }
}