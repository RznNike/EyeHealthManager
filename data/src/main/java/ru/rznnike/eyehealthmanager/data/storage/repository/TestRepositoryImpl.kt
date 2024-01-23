package ru.rznnike.eyehealthmanager.data.storage.repository

import io.objectbox.BoxStore
import ru.rznnike.eyehealthmanager.data.storage.dao.AcuityTestDAO
import ru.rznnike.eyehealthmanager.data.storage.dao.AstigmatismTestDAO
import ru.rznnike.eyehealthmanager.data.storage.dao.ColorPerceptionTestDAO
import ru.rznnike.eyehealthmanager.data.storage.dao.ContrastTestDAO
import ru.rznnike.eyehealthmanager.data.storage.dao.DaltonismTestDAO
import ru.rznnike.eyehealthmanager.data.storage.dao.NearFarTestDAO
import ru.rznnike.eyehealthmanager.data.storage.dao.TestDAO
import ru.rznnike.eyehealthmanager.data.storage.entity.TestEntity
import ru.rznnike.eyehealthmanager.data.storage.entity.toAcuityTestEntity
import ru.rznnike.eyehealthmanager.data.storage.entity.toAstigmatismTestEntity
import ru.rznnike.eyehealthmanager.data.storage.entity.toColorPerceptionTestEntity
import ru.rznnike.eyehealthmanager.data.storage.entity.toContrastTestEntity
import ru.rznnike.eyehealthmanager.data.storage.entity.toDaltonismTestEntity
import ru.rznnike.eyehealthmanager.data.storage.entity.toNearFarTestEntity
import ru.rznnike.eyehealthmanager.domain.model.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.AstigmatismTestResult
import ru.rznnike.eyehealthmanager.domain.model.ColorPerceptionTestResult
import ru.rznnike.eyehealthmanager.domain.model.ContrastTestResult
import ru.rznnike.eyehealthmanager.domain.model.DaltonismTestResult
import ru.rznnike.eyehealthmanager.domain.model.NearFarTestResult
import ru.rznnike.eyehealthmanager.domain.model.TestResult
import ru.rznnike.eyehealthmanager.domain.model.TestResultPagingParameters
import ru.rznnike.eyehealthmanager.domain.model.enums.TestType
import ru.rznnike.eyehealthmanager.domain.storage.repository.TestRepository

class TestRepositoryImpl(
    private val boxStore: BoxStore,
    private val testDAO: TestDAO,
    private val acuityTestDAO: AcuityTestDAO,
    private val astigmatismTestDAO: AstigmatismTestDAO,
    private val colorPerceptionTestDAO: ColorPerceptionTestDAO,
    private val contrastTestDAO: ContrastTestDAO,
    private val daltonismTestDAO: DaltonismTestDAO,
    private val nearFarTestDAO: NearFarTestDAO
) : TestRepository {
    override suspend fun getList(parameters: TestResultPagingParameters) =
        testDAO.getList(parameters).mapNotNull { it.toTestResult() }

    override suspend fun getListDistinctByType() =
        testDAO.getListDistinctByType().mapNotNull { it.toTestResult() }

    override suspend fun add(items: List<TestResult>) =
        boxStore.runInTx {
            items.forEach { addTestToDB(it) }
        }

    override suspend fun add(item: TestResult): Long {
        var resultId: Long = 0
        boxStore.runInTx {
            resultId = addTestToDB(item)
        }
        return resultId
    }

    override suspend fun delete(id: Long) =
        deleteTestFromDB(id)

    override suspend fun deleteAll() =
        boxStore.runInTx {
            listOf(
                testDAO,
                acuityTestDAO,
                astigmatismTestDAO,
                nearFarTestDAO,
                colorPerceptionTestDAO,
                daltonismTestDAO,
                contrastTestDAO
            ).forEach {
                it.deleteAll()
            }
        }

    override suspend fun deleteDuplicates() =
        boxStore.runInTx {
            var lastId = 0L
            do {
                val baseEntity = testDAO.getFirstNewerById(lastId)
                lastId = baseEntity?.id ?: -1
                baseEntity?.let {
                    val similarEntities = testDAO.getAllNewerSimilar(baseEntity)
                    baseEntity.toTestResult()?.let { baseResult ->
                        similarEntities.mapNotNull { it.toTestResult() }
                            .forEach { similarResult ->
                                if (baseResult.contentEquals(similarResult)) {
                                    deleteTestFromDB(similarResult.id)
                                }
                            }
                    }
                }
            } while (lastId >= 0)
        }

    private fun addTestToDB(item: TestResult): Long {
        var relationId = 0L
        var testType = TestType.ACUITY
        when (item) {
            is AcuityTestResult -> {
                testType = TestType.ACUITY
                relationId = acuityTestDAO.add(item.toAcuityTestEntity())
            }
            is AstigmatismTestResult -> {
                testType = TestType.ASTIGMATISM
                relationId = astigmatismTestDAO.add(item.toAstigmatismTestEntity())
            }
            is NearFarTestResult -> {
                testType = TestType.NEAR_FAR
                relationId = nearFarTestDAO.add(item.toNearFarTestEntity())
            }
            is ColorPerceptionTestResult -> {
                testType = TestType.COLOR_PERCEPTION
                relationId = colorPerceptionTestDAO.add(item.toColorPerceptionTestEntity())
            }
            is DaltonismTestResult -> {
                testType = TestType.DALTONISM
                relationId = daltonismTestDAO.add(item.toDaltonismTestEntity())
            }
            is ContrastTestResult -> {
                testType = TestType.CONTRAST
                relationId = contrastTestDAO.add(item.toContrastTestEntity())
            }
        }
        return testDAO.add(
            TestEntity(
                testType = testType,
                relationId = relationId,
                timestamp = item.timestamp / 1000 * 1000 // remove ms
            )
        )
    }

    private fun deleteTestFromDB(id: Long) =
        boxStore.runInTx {
            val testEntity = testDAO.get(id)
            testDAO.delete(id)
            testEntity?.let {
                when (testEntity.testType) {
                    TestType.ACUITY -> acuityTestDAO
                    TestType.ASTIGMATISM -> astigmatismTestDAO
                    TestType.NEAR_FAR -> nearFarTestDAO
                    TestType.COLOR_PERCEPTION -> colorPerceptionTestDAO
                    TestType.DALTONISM -> daltonismTestDAO
                    TestType.CONTRAST -> contrastTestDAO
                }.delete(testEntity.relationId)
            }
        }

    private fun TestEntity.toTestResult() =
        when (testType) {
            TestType.ACUITY -> acuityTestDAO.get(relationId)?.toAcuityTestResult(this)
            TestType.ASTIGMATISM -> astigmatismTestDAO.get(relationId)?.toAstigmatismTestResult(this)
            TestType.NEAR_FAR -> nearFarTestDAO.get(relationId)?.toNearFarTestResult(this)
            TestType.COLOR_PERCEPTION -> colorPerceptionTestDAO.get(relationId)?.toColorPerceptionTestResult(this)
            TestType.DALTONISM -> daltonismTestDAO.get(relationId)?.toDaltonismTestResult(this)
            TestType.CONTRAST -> contrastTestDAO.get(relationId)?.toContrastTestResult(this)
        }
}
