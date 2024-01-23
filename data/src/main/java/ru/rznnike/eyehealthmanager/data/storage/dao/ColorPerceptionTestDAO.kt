package ru.rznnike.eyehealthmanager.data.storage.dao

import io.objectbox.BoxStore
import ru.rznnike.eyehealthmanager.data.storage.entity.ColorPerceptionTestEntity
import ru.rznnike.eyehealthmanager.data.storage.global.BaseDAO
import ru.rznnike.eyehealthmanager.data.storage.global.ITestDAO

class ColorPerceptionTestDAO(
    boxStore: BoxStore
) : BaseDAO<ColorPerceptionTestEntity>(boxStore, ColorPerceptionTestEntity::class),
    ITestDAO<ColorPerceptionTestEntity> {
    override fun get(id: Long): ColorPerceptionTestEntity? = box.get(id)

    override fun add(entity: ColorPerceptionTestEntity) = box.put(entity)

    override fun delete(id: Long) = box.remove(id)

    override fun deleteAll() = box.removeAll()
}