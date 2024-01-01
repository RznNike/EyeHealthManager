package ru.rznnike.eyehealthmanager.data.storage.entity

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import ru.rznnike.eyehealthmanager.data.storage.converter.DaltonismAnomalyTypeConverter
import ru.rznnike.eyehealthmanager.domain.model.DaltonismTestResult
import ru.rznnike.eyehealthmanager.domain.model.enums.DaltonismAnomalyType

@Entity
data class DaltonismTestEntity(
    @Id var id: Long = 0,
    val errorsCount: Int,
    @Convert(converter = DaltonismAnomalyTypeConverter::class, dbType = Int::class)
    val anomalyType: DaltonismAnomalyType
) {
    fun toDaltonismTestResult(parentEntity: TestEntity) = DaltonismTestResult(
        id = parentEntity.id,
        timestamp = parentEntity.timestamp,
        errorsCount = errorsCount,
        anomalyType = anomalyType
    )
}

fun DaltonismTestResult.toDaltonismTestEntity() = DaltonismTestEntity(
    errorsCount = errorsCount,
    anomalyType = anomalyType
)