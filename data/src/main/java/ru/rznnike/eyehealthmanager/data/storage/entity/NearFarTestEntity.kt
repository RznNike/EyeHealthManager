package ru.rznnike.eyehealthmanager.data.storage.entity

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import ru.rznnike.eyehealthmanager.data.storage.converter.NearFarAnswerTypeConverter
import ru.rznnike.eyehealthmanager.domain.model.test.nearfar.NearFarTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.nearfar.NearFarAnswerType

@Entity
data class NearFarTestEntity(
    @Id var id: Long = 0,
    @Convert(converter = NearFarAnswerTypeConverter::class, dbType = Int::class)
    val resultLeftEye: NearFarAnswerType,
    @Convert(converter = NearFarAnswerTypeConverter::class, dbType = Int::class)
    val resultRightEye: NearFarAnswerType
) {
    fun toNearFarTestResult(parentEntity: TestEntity) = NearFarTestResult(
        id = parentEntity.id,
        timestamp = parentEntity.timestamp,
        resultLeftEye = resultLeftEye,
        resultRightEye = resultRightEye
    )
}

fun NearFarTestResult.toNearFarTestEntity() = NearFarTestEntity(
    resultLeftEye = resultLeftEye,
    resultRightEye = resultRightEye
)