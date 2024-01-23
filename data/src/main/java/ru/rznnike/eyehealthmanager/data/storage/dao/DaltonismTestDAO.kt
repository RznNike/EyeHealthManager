package ru.rznnike.eyehealthmanager.data.storage.dao

import io.objectbox.BoxStore
import ru.rznnike.eyehealthmanager.data.storage.entity.DaltonismTestEntity
import ru.rznnike.eyehealthmanager.data.storage.global.BaseDAO
import ru.rznnike.eyehealthmanager.data.storage.global.ITestDAO

class DaltonismTestDAO(
    boxStore: BoxStore
) : BaseDAO<DaltonismTestEntity>(boxStore, DaltonismTestEntity::class),
    ITestDAO<DaltonismTestEntity> {
    override fun get(id: Long): DaltonismTestEntity? = box.get(id)

    override fun add(entity: DaltonismTestEntity) = box.put(entity)

    override fun delete(id: Long) = box.remove(id)

    override fun deleteAll() = box.removeAll()
}