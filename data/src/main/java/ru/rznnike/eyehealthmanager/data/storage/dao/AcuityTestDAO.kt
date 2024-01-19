package ru.rznnike.eyehealthmanager.data.storage.dao

import io.objectbox.BoxStore
import ru.rznnike.eyehealthmanager.data.storage.entity.AcuityTestEntity
import ru.rznnike.eyehealthmanager.data.storage.global.BaseDAO
import ru.rznnike.eyehealthmanager.data.storage.global.ITestDAO

class AcuityTestDAO(
    boxStore: BoxStore
) : BaseDAO<AcuityTestEntity>(boxStore, AcuityTestEntity::class), ITestDAO<AcuityTestEntity> {
    override fun get(id: Long): AcuityTestEntity? = box.get(id)

    override fun add(entity: AcuityTestEntity) = box.put(entity)

    override fun delete(id: Long) = box.remove(id)

    override fun deleteAll() = box.removeAll()
}