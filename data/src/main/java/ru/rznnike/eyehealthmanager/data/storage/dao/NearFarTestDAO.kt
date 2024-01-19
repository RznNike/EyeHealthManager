package ru.rznnike.eyehealthmanager.data.storage.dao

import io.objectbox.BoxStore
import ru.rznnike.eyehealthmanager.data.storage.entity.NearFarTestEntity
import ru.rznnike.eyehealthmanager.data.storage.global.BaseDAO
import ru.rznnike.eyehealthmanager.data.storage.global.ITestDAO

class NearFarTestDAO(
    boxStore: BoxStore
) : BaseDAO<NearFarTestEntity>(boxStore, NearFarTestEntity::class), ITestDAO<NearFarTestEntity> {
    override fun get(id: Long): NearFarTestEntity? = box.get(id)

    override fun add(entity: NearFarTestEntity) = box.put(entity)

    override fun delete(id: Long) = box.remove(id)

    override fun deleteAll() = box.removeAll()
}