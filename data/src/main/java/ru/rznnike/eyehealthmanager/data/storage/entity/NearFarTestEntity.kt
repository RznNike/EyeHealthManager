package ru.rznnike.eyehealthmanager.data.storage.entity

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import ru.rznnike.eyehealthmanager.data.storage.converter.NearFarAnswerTypeConverter
import ru.rznnike.eyehealthmanager.domain.model.enums.NearFarAnswerType

@Entity
data class NearFarTestEntity(
    @Id var id: Long = 0,
    @Convert(converter = NearFarAnswerTypeConverter::class, dbType = Int::class)
    val resultLeftEye: NearFarAnswerType? = null,
    @Convert(converter = NearFarAnswerTypeConverter::class, dbType = Int::class)
    val resultRightEye: NearFarAnswerType? = null
)