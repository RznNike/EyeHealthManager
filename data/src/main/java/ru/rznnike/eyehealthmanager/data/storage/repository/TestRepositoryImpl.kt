package ru.rznnike.eyehealthmanager.data.storage.repository

import io.objectbox.BoxStore
import ru.rznnike.eyehealthmanager.data.storage.converter.TestTypeConverter
import ru.rznnike.eyehealthmanager.data.storage.entity.*
import ru.rznnike.eyehealthmanager.data.storage.global.BaseRepository
import ru.rznnike.eyehealthmanager.domain.model.*
import ru.rznnike.eyehealthmanager.domain.model.enums.TestType

class TestRepositoryImpl(
    boxStore: BoxStore
) : BaseRepository<TestEntity>(boxStore, TestEntity::class.java), TestRepository {
    override suspend fun getTests(params: TestResultPagingParams): List<TestResult> {
        val queryBuilder = box.query()
            .notNull(TestEntity_.relationId)
            .orderDesc(TestEntity_.timestamp)

        params.filterParams?.let { filterParams ->
            if (filterParams.filterByDate) {
                queryBuilder
                    .between(TestEntity_.timestamp, filterParams.dateFrom, filterParams.dateTo)
            }
            if (filterParams.filterByType && (filterParams.selectedTestTypes.isNotEmpty())) {
                val converter = TestTypeConverter()
                queryBuilder
                    .`in`(
                        TestEntity_.testType,
                        filterParams.selectedTestTypes
                            .map {
                                converter.convertToDatabaseValue(it)
                            }
                            .toIntArray()
                    )
            }
        }

        val testEntities = queryBuilder
            .build()
            .find(params.offset.toLong(), params.offset.toLong())

        return testEntities.map { mapTestEntityToTestResult(it) }
    }

    override suspend fun getAllLastTests(): List<TestResult> {
        val testEntities = mutableListOf<TestEntity>()
        val converter = TestTypeConverter()
        TestType.entries.forEach { type ->
            val entity = box.query()
                .notNull(TestEntity_.relationId)
                .orderDesc(TestEntity_.timestamp)
                .equal(TestEntity_.testType, converter.convertToDatabaseValue(type).toLong())
                .build()
                .findFirst()
            entity?.let {
                testEntities.add(it)
            }
        }

        return testEntities.map { mapTestEntityToTestResult(it) }
    }

    private fun mapTestEntityToTestResult(it: TestEntity): TestResult {
        return when (it.testType) {
            TestType.ACUITY -> {
                val acuityTestEntity = box.store.boxFor(AcuityTestEntity::class.java)
                    .get(it.relationId)
                AcuityTestResult(
                    id = it.id,
                    timestamp = it.timestamp,
                    symbolsType = acuityTestEntity.symbolsType,
                    testEyesType = acuityTestEntity.testEyesType,
                    dayPart = acuityTestEntity.dayPart,
                    resultLeftEye = acuityTestEntity.resultLeftEye,
                    resultRightEye = acuityTestEntity.resultRightEye,
                    measuredByDoctor = acuityTestEntity.measuredByDoctor
                )
            }
            TestType.ASTIGMATISM -> {
                val astigmatismTestEntity = box.store.boxFor(AstigmatismTestEntity::class.java)
                    .get(it.relationId)
                AstigmatismTestResult(
                    id = it.id,
                    timestamp = it.timestamp,
                    resultLeftEye = astigmatismTestEntity.resultLeftEye,
                    resultRightEye = astigmatismTestEntity.resultRightEye
                )
            }
            TestType.NEAR_FAR -> {
                val nearFarTestEntity = box.store.boxFor(NearFarTestEntity::class.java)
                    .get(it.relationId)
                NearFarTestResult(
                    id = it.id,
                    timestamp = it.timestamp,
                    resultLeftEye = nearFarTestEntity.resultLeftEye,
                    resultRightEye = nearFarTestEntity.resultRightEye
                )
            }
            TestType.COLOR_PERCEPTION -> {
                val colorPerceptionTestEntity =
                    box.store.boxFor(ColorPerceptionTestEntity::class.java)
                        .get(it.relationId)
                ColorPerceptionTestResult(
                    id = it.id,
                    timestamp = it.timestamp,
                    recognizedColorsCount = colorPerceptionTestEntity.recognizedColorsCount,
                    allColorsCount = colorPerceptionTestEntity.allColorsCount
                )
            }
            TestType.DALTONISM -> {
                val daltonismTestEntity = box.store.boxFor(DaltonismTestEntity::class.java)
                    .get(it.relationId)
                DaltonismTestResult(
                    id = it.id,
                    timestamp = it.timestamp,
                    errorsCount = daltonismTestEntity.errorsCount,
                    anomalyType = daltonismTestEntity.anomalyType
                )
            }
            TestType.CONTRAST -> {
                val contrastTestEntity = box.store.boxFor(ContrastTestEntity::class.java)
                    .get(it.relationId)
                ContrastTestResult(
                    id = it.id,
                    timestamp = it.timestamp,
                    recognizedContrast = contrastTestEntity.recognizedContrast
                )
            }
        }
    }

    override suspend fun addTests(items: List<TestResult>) {
        items.forEach {
            addTest(it)
        }
    }

    override suspend fun addTest(item: TestResult): Long {
        var resultId: Long = 0
        box.store.runInTx {
            item.timestamp = item.timestamp / 1000 * 1000 // remove ms
            var relationId = 0L
            var testType = TestType.ACUITY
            when (item) {
                is AcuityTestResult -> {
                    testType = TestType.ACUITY
                    val acuityTestEntity = AcuityTestEntity(
                        symbolsType = item.symbolsType,
                        testEyesType = item.testEyesType,
                        dayPart = item.dayPart,
                        resultLeftEye = item.resultLeftEye,
                        resultRightEye = item.resultRightEye,
                        measuredByDoctor = item.measuredByDoctor
                    )
                    relationId = box.store.boxFor(AcuityTestEntity::class.java).put(acuityTestEntity)
                }
                is AstigmatismTestResult -> {
                    testType = TestType.ASTIGMATISM
                    val astigmatismTestEntity = AstigmatismTestEntity(
                        resultLeftEye = item.resultLeftEye,
                        resultRightEye = item.resultRightEye
                    )
                    relationId = box.store.boxFor(AstigmatismTestEntity::class.java).put(astigmatismTestEntity)
                }
                is NearFarTestResult -> {
                    testType = TestType.NEAR_FAR
                    val nearFarTestEntity = NearFarTestEntity(
                        resultLeftEye = item.resultLeftEye,
                        resultRightEye = item.resultRightEye
                    )
                    relationId = box.store.boxFor(NearFarTestEntity::class.java).put(nearFarTestEntity)
                }
                is ColorPerceptionTestResult -> {
                    testType = TestType.COLOR_PERCEPTION
                    val colorPerceptionTestEntity = ColorPerceptionTestEntity(
                        recognizedColorsCount = item.recognizedColorsCount,
                        allColorsCount = item.allColorsCount
                    )
                    relationId = box.store.boxFor(ColorPerceptionTestEntity::class.java).put(colorPerceptionTestEntity)
                }
                is DaltonismTestResult -> {
                    testType = TestType.DALTONISM
                    val daltonismTestEntity = DaltonismTestEntity(
                        errorsCount = item.errorsCount,
                        anomalyType = item.anomalyType
                    )
                    relationId = box.store.boxFor(DaltonismTestEntity::class.java).put(daltonismTestEntity)
                }
                is ContrastTestResult -> {
                    testType = TestType.CONTRAST
                    val contrastTestEntity = ContrastTestEntity(
                        recognizedContrast = item.recognizedContrast
                    )
                    relationId = box.store.boxFor(ContrastTestEntity::class.java).put(contrastTestEntity)
                }
            }
            val testEntity = TestEntity(
                testType = testType,
                relationId = relationId,
                timestamp = item.timestamp
            )
            resultId = box.put(testEntity)
        }

        return resultId
    }

    override suspend fun deleteTestById(id: Long) {
        box.store.runInTx {
            deleteTest(id)
        }
    }

    private fun deleteTest(id: Long) {
        val testEntity = box.query()
            .equal(TestEntity_.id, id)
            .build()
            .findFirst()
        box.remove(id)
        when (testEntity?.testType) {
            TestType.ACUITY -> {
                box.store.boxFor(AcuityTestEntity::class.java)
                    .remove(testEntity.relationId)
            }
            TestType.ASTIGMATISM -> {
                box.store.boxFor(AstigmatismTestEntity::class.java)
                    .remove(testEntity.relationId)
            }
            TestType.NEAR_FAR -> {
                box.store.boxFor(NearFarTestEntity::class.java)
                    .remove(testEntity.relationId)
            }
            TestType.COLOR_PERCEPTION -> {
                box.store.boxFor(ColorPerceptionTestEntity::class.java)
                    .remove(testEntity.relationId)
            }
            TestType.DALTONISM -> {
                box.store.boxFor(DaltonismTestEntity::class.java)
                    .remove(testEntity.relationId)
            }
            TestType.CONTRAST -> {
                box.store.boxFor(ContrastTestEntity::class.java)
                    .remove(testEntity.relationId)
            }
            null -> Unit
        }
    }

    override suspend fun deleteAllTests() {
        box.store.runInTx {
            box.removeAll()
            box.store.boxFor(TestEntity::class.java).removeAll()
            box.store.boxFor(AcuityTestEntity::class.java).removeAll()
            box.store.boxFor(AstigmatismTestEntity::class.java).removeAll()
            box.store.boxFor(NearFarTestEntity::class.java).removeAll()
            box.store.boxFor(ColorPerceptionTestEntity::class.java).removeAll()
            box.store.boxFor(DaltonismTestEntity::class.java).removeAll()
            box.store.boxFor(ContrastTestEntity::class.java).removeAll()
        }
    }

    override suspend fun deleteDuplicates() {
        box.store.runInTx {
            val testTypeConverter = TestTypeConverter()

            var lastId = 0L
            var baseEntity: TestEntity?
            do {
                baseEntity = box.query()
                    .notNull(TestEntity_.relationId)
                    .order(TestEntity_.id)
                    .greater(TestEntity_.id, lastId)
                    .build()
                    .findFirst()
                baseEntity?.let {
                    lastId = baseEntity.id

                    val similarEntities = box.query()
                        .notNull(TestEntity_.relationId)
                        .order(TestEntity_.id)
                        .equal(TestEntity_.timestamp, baseEntity.timestamp)
                        .equal(TestEntity_.testType, testTypeConverter.convertToDatabaseValue(baseEntity.testType).toLong())
                        .greater(TestEntity_.id, lastId)
                        .build()
                        .find()

                    val baseResult = mapTestEntityToTestResult(baseEntity)
                    similarEntities.forEach {
                        val similarResult = mapTestEntityToTestResult(it)
                        if (baseResult.contentEquals(similarResult)) {
                            deleteTest(similarResult.id)
                        }
                    }
                }
            } while (baseEntity != null)
        }
    }
}
