package ru.rznnike.eyehealthmanager.data.storage.repository

import io.objectbox.BoxStore
import ru.rznnike.eyehealthmanager.data.storage.converter.TestTypeConverter
import ru.rznnike.eyehealthmanager.data.storage.entity.*
import ru.rznnike.eyehealthmanager.data.storage.global.BaseRepository
import ru.rznnike.eyehealthmanager.domain.model.*
import ru.rznnike.eyehealthmanager.domain.model.enums.TestType

class TestRepositoryImpl(
    private val boxStore: BoxStore
) : BaseRepository<TestEntity>(boxStore, TestEntity::class.java), TestRepository {
    private val boxAcuity = boxStore.boxFor(AcuityTestEntity::class.java)
    private val boxAstigmatism = boxStore.boxFor(AstigmatismTestEntity::class.java)
    private val boxNearFar = boxStore.boxFor(NearFarTestEntity::class.java)
    private val boxColorPerception = boxStore.boxFor(ColorPerceptionTestEntity::class.java)
    private val boxDaltonism = boxStore.boxFor(DaltonismTestEntity::class.java)
    private val boxContrast = boxStore.boxFor(ContrastTestEntity::class.java)

    override suspend fun getTests(parameters: TestResultPagingParameters): List<TestResult> {
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

        val testEntities = queryBuilder
            .build()
            .find(parameters.offset.toLong(), parameters.limit.toLong())

        return testEntities.mapNotNull { mapTestEntityToTestResult(it) }
    }

    override suspend fun getAllLastTests(): List<TestResult> {
        val converter = TestTypeConverter()
        return TestType.entries.mapNotNull { type ->
            box.query()
                .notNull(TestEntity_.relationId)
                .orderDesc(TestEntity_.timestamp)
                .equal(TestEntity_.testType, converter.convertToDatabaseValue(type).toLong())
                .build()
                .findFirst()
                ?.let {
                    mapTestEntityToTestResult(it)
                }
        }
    }

    private fun mapTestEntityToTestResult(it: TestEntity) =
        when (it.testType) {
            TestType.ACUITY -> boxAcuity.get(it.relationId)?.toAcuityTestResult(it)
            TestType.ASTIGMATISM -> boxAstigmatism.get(it.relationId)?.toAstigmatismTestResult(it)
            TestType.NEAR_FAR -> boxNearFar.get(it.relationId)?.toNearFarTestResult(it)
            TestType.COLOR_PERCEPTION -> boxColorPerception.get(it.relationId)?.toColorPerceptionTestResult(it)
            TestType.DALTONISM -> boxDaltonism.get(it.relationId)?.toDaltonismTestResult(it)
            TestType.CONTRAST -> boxContrast.get(it.relationId)?.toContrastTestResult(it)
        }

    override suspend fun addTests(items: List<TestResult>) =
        boxStore.runInTx {
            items.forEach { putTestToDB(it) }
        }

    override suspend fun addTest(item: TestResult): Long {
        var resultId: Long = 0
        boxStore.runInTx {
            resultId = putTestToDB(item)
        }
        return resultId
    }

    private fun putTestToDB(item: TestResult): Long {
        var relationId = 0L
        var testType = TestType.ACUITY
        when (item) {
            is AcuityTestResult -> {
                testType = TestType.ACUITY
                relationId = boxAcuity.put(item.toAcuityTestEntity())
            }
            is AstigmatismTestResult -> {
                testType = TestType.ASTIGMATISM
                relationId = boxAstigmatism.put(item.toAstigmatismTestEntity())
            }
            is NearFarTestResult -> {
                testType = TestType.NEAR_FAR
                relationId = boxNearFar.put(item.toNearFarTestEntity())
            }
            is ColorPerceptionTestResult -> {
                testType = TestType.COLOR_PERCEPTION
                relationId = boxColorPerception.put(item.toColorPerceptionTestEntity())
            }
            is DaltonismTestResult -> {
                testType = TestType.DALTONISM
                relationId = boxDaltonism.put(item.toDaltonismTestEntity())
            }
            is ContrastTestResult -> {
                testType = TestType.CONTRAST
                relationId = boxContrast.put(item.toContrastTestEntity())
            }
        }
        val testEntity = TestEntity(
            testType = testType,
            relationId = relationId,
            timestamp = item.timestamp / 1000 * 1000 // remove ms
        )
        return box.put(testEntity)
    }

    override suspend fun deleteTestById(id: Long) = boxStore.runInTx { deleteTest(id) }

    private fun deleteTest(id: Long) {
        val testEntity = box.query()
            .equal(TestEntity_.id, id)
            .build()
            .findFirst()
        box.remove(id)
        testEntity?.let {
            when (testEntity.testType) {
                TestType.ACUITY -> boxAcuity
                TestType.ASTIGMATISM -> boxAstigmatism
                TestType.NEAR_FAR -> boxNearFar
                TestType.COLOR_PERCEPTION -> boxColorPerception
                TestType.DALTONISM -> boxDaltonism
                TestType.CONTRAST -> boxContrast
            }.remove(testEntity.relationId)
        }
    }

    override suspend fun deleteAllTests() {
        boxStore.runInTx {
            listOf(
                box,
                boxAcuity,
                boxAstigmatism,
                boxNearFar,
                boxColorPerception,
                boxDaltonism,
                boxContrast
            ).forEach {
                it.removeAll()
            }
        }
    }

    override suspend fun deleteDuplicates() {
        boxStore.runInTx {
            val testTypeConverter = TestTypeConverter()

            var lastId = 0L
            do {
                box.query()
                    .notNull(TestEntity_.relationId)
                    .order(TestEntity_.id)
                    .greater(TestEntity_.id, lastId)
                    .build()
                    .findFirst()
                    ?.let { baseEntity ->
                        lastId = baseEntity.id

                        val similarEntities = box.query()
                            .notNull(TestEntity_.relationId)
                            .order(TestEntity_.id)
                            .equal(TestEntity_.timestamp, baseEntity.timestamp)
                            .equal(TestEntity_.testType, testTypeConverter.convertToDatabaseValue(baseEntity.testType).toLong())
                            .greater(TestEntity_.id, lastId)
                            .build()
                            .find()

                        mapTestEntityToTestResult(baseEntity)?.let { baseResult ->
                            similarEntities.forEach {
                                mapTestEntityToTestResult(it)?.let { similarResult ->
                                    if (baseResult.contentEquals(similarResult)) {
                                        deleteTest(similarResult.id)
                                    }
                                }
                            }
                        }
                    }
                    ?: run {
                        lastId = -1
                    }
            } while (lastId >= 0)
        }
    }
}
