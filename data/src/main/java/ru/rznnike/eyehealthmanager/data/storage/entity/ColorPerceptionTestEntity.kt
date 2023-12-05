package ru.rznnike.eyehealthmanager.data.storage.entity

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class ColorPerceptionTestEntity(
    @Id var id: Long = 0,
    val recognizedColorsCount: Int,
    val allColorsCount: Int
)