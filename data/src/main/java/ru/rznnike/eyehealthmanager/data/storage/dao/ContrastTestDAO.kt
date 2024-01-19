package ru.rznnike.eyehealthmanager.data.storage.dao

import io.objectbox.BoxStore
import ru.rznnike.eyehealthmanager.data.storage.entity.ContrastTestEntity
import ru.rznnike.eyehealthmanager.data.storage.global.BaseDAO
import ru.rznnike.eyehealthmanager.data.storage.global.ITestDAO

class ContrastTestDAO(
    boxStore: BoxStore
) : BaseDAO<ContrastTestEntity>(boxStore, ContrastTestEntity::class), ITestDAO<ContrastTestEntity> {
    override fun get(id: Long): ContrastTestEntity? = box.get(id)

    override fun add(entity: ContrastTestEntity) = box.put(entity)

    override fun delete(id: Long) = box.remove(id)

    override fun deleteAll() = box.removeAll()
}