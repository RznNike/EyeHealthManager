package ru.rznnike.eyehealthmanager.data.storage.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import ru.rznnike.eyehealthmanager.domain.model.test.contrast.ContrastTestResult

@Entity
data class ContrastTestEntity(
    @Id var id: Long = 0,
    val recognizedContrast: Int = 0
) {
    fun toContrastTestResult(parentEntity: TestEntity) = ContrastTestResult(
        id = parentEntity.id,
        timestamp = parentEntity.timestamp,
        recognizedContrast = recognizedContrast
    )
}

fun ContrastTestResult.toContrastTestEntity() = ContrastTestEntity(
    recognizedContrast = recognizedContrast
)