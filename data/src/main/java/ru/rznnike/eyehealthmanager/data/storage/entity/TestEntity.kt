package ru.rznnike.eyehealthmanager.data.storage.entity

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import ru.rznnike.eyehealthmanager.data.storage.converter.TestTypeConverter
import ru.rznnike.eyehealthmanager.domain.model.enums.TestType

@Entity
data class TestEntity(
    @Id var id: Long = 0,
    @Convert(converter = TestTypeConverter::class, dbType = Int::class)
    val testType: TestType = TestType.ACUITY,
    val relationId: Long = 0,
    val timestamp: Long = 0
)