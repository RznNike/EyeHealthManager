package ru.rznnike.eyehealthmanager.data.storage.entity

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import ru.rznnike.eyehealthmanager.data.storage.converter.AstigmatismAnswerTypeConverter
import ru.rznnike.eyehealthmanager.domain.model.test.astigmatism.AstigmatismTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.astigmatism.AstigmatismAnswerType

@Entity
data class AstigmatismTestEntity(
    @Id var id: Long = 0,
    @Convert(converter = AstigmatismAnswerTypeConverter::class, dbType = Int::class)
    val resultLeftEye: AstigmatismAnswerType? = null,
    @Convert(converter = AstigmatismAnswerTypeConverter::class, dbType = Int::class)
    val resultRightEye: AstigmatismAnswerType? = null
) {
    fun toAstigmatismTestResult(parentEntity: TestEntity) = AstigmatismTestResult(
        id = parentEntity.id,
        timestamp = parentEntity.timestamp,
        resultLeftEye = resultLeftEye,
        resultRightEye = resultRightEye
    )
}

fun AstigmatismTestResult.toAstigmatismTestEntity() = AstigmatismTestEntity(
    resultLeftEye = resultLeftEye,
    resultRightEye = resultRightEye
)