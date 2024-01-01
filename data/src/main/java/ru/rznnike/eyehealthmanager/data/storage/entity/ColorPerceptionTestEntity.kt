package ru.rznnike.eyehealthmanager.data.storage.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import ru.rznnike.eyehealthmanager.domain.model.ColorPerceptionTestResult

@Entity
data class ColorPerceptionTestEntity(
    @Id var id: Long = 0,
    val recognizedColorsCount: Int,
    val allColorsCount: Int
) {
    fun toColorPerceptionTestResult(parentEntity: TestEntity) = ColorPerceptionTestResult(
        id = parentEntity.id,
        timestamp = parentEntity.timestamp,
        recognizedColorsCount = recognizedColorsCount,
        allColorsCount = allColorsCount
    )
}

fun ColorPerceptionTestResult.toColorPerceptionTestEntity() = ColorPerceptionTestEntity(
    recognizedColorsCount = recognizedColorsCount,
    allColorsCount = allColorsCount
)