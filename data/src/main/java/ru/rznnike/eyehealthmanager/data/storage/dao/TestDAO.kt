package ru.rznnike.eyehealthmanager.data.storage.dao

import io.objectbox.BoxStore
import ru.rznnike.eyehealthmanager.data.storage.converter.TestTypeConverter
import ru.rznnike.eyehealthmanager.data.storage.entity.TestEntity
import ru.rznnike.eyehealthmanager.data.storage.entity.TestEntity_
import ru.rznnike.eyehealthmanager.data.storage.global.BaseDAO
import ru.rznnike.eyehealthmanager.data.storage.global.ITestDAO
import ru.rznnike.eyehealthmanager.domain.model.TestResultPagingParameters
import ru.rznnike.eyehealthmanager.domain.model.enums.TestType

class TestDAO(
    boxStore: BoxStore
) : BaseDAO<TestEntity>(boxStore, TestEntity::class), ITestDAO<TestEntity> {
    override fun get(id: Long): TestEntity? = box.get(id)

    override fun add(entity: TestEntity) = box.put(entity)

    override fun delete(id: Long) = box.remove(id)

    override fun deleteAll() = box.removeAll()

    fun getList(parameters: TestResultPagingParameters): List<TestEntity> {
        val queryBuilder = box.query()
            .notNull(TestEntity_.relationId)
            .orderDesc(TestEntity_.timestamp)

        parameters.filter?.let { filter ->
            if (filter.filterByDate) {
                queryBuilder
                    .between(TestEntity_.timestamp, filter.dateFrom, filter.dateTo)
            }
            if (filter.filterByType && (filter.selectedTestTypes.isNotEmpty())) {
                val converter = TestTypeConverter()
                queryBuilder
                    .`in`(
                        TestEntity_.testType,
                        filter.selectedTestTypes
                            .map { converter.convertToDatabaseValue(it) }
                            .toIntArray()
                    )
            }
        }

        return queryBuilder
            .build()
            .find(parameters.offset.toLong(), parameters.limit.toLong())
    }

    fun getListDistinctByType(): List<TestEntity> {
        val converter = TestTypeConverter()
        return TestType.entries.mapNotNull { type ->
            box.query()
                .notNull(TestEntity_.relationId)
                .orderDesc(TestEntity_.timestamp)
                .equal(TestEntity_.testType, converter.convertToDatabaseValue(type).toLong())
                .build()
                .findFirst()
        }
    }

    fun getFirstNewerById(id: Long): TestEntity? = box.query()
        .notNull(TestEntity_.relationId)
        .order(TestEntity_.id)
        .greater(TestEntity_.id, id)
        .build()
        .findFirst()

    fun getAllNewerSimilar(entity: TestEntity): List<TestEntity> {
        val converter = TestTypeConverter()
        return box.query()
            .notNull(TestEntity_.relationId)
            .order(TestEntity_.id)
            .equal(TestEntity_.timestamp, entity.timestamp)
            .equal(TestEntity_.testType, converter.convertToDatabaseValue(entity.testType).toLong())
            .greater(TestEntity_.id, entity.id)
            .build()
            .find()
    }
}
