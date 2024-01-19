package ru.rznnike.eyehealthmanager.data.storage.dao

import io.objectbox.BoxStore
import ru.rznnike.eyehealthmanager.data.storage.entity.AstigmatismTestEntity
import ru.rznnike.eyehealthmanager.data.storage.global.BaseDAO
import ru.rznnike.eyehealthmanager.data.storage.global.ITestDAO

class AstigmatismTestDAO(
    boxStore: BoxStore
) : BaseDAO<AstigmatismTestEntity>(boxStore, AstigmatismTestEntity::class),
    ITestDAO<AstigmatismTestEntity> {
    override fun get(id: Long): AstigmatismTestEntity? = box.get(id)

    override fun add(entity: AstigmatismTestEntity) = box.put(entity)

    override fun delete(id: Long) = box.remove(id)

    override fun deleteAll() = box.removeAll()
}