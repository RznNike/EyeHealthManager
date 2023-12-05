package ru.rznnike.eyehealthmanager.data.storage.entity

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import ru.rznnike.eyehealthmanager.data.storage.converter.AstigmatismAnswerTypeConverter
import ru.rznnike.eyehealthmanager.domain.model.enums.AstigmatismAnswerType

@Entity
data class AstigmatismTestEntity(
    @Id var id: Long = 0,
    @Convert(converter = AstigmatismAnswerTypeConverter::class, dbType = Int::class)
    val resultLeftEye: AstigmatismAnswerType? = null,
    @Convert(converter = AstigmatismAnswerTypeConverter::class, dbType = Int::class)
    val resultRightEye: AstigmatismAnswerType? = null
)