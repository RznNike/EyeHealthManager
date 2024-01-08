package ru.rznnike.eyehealthmanager.data.storage.dao

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.rznnike.eyehealthmanager.data.storage.AbstractObjectBoxTest
import ru.rznnike.eyehealthmanager.data.storage.entity.TestEntity
import ru.rznnike.eyehealthmanager.domain.model.enums.TestType

class TestDAOTest : AbstractObjectBoxTest() {
    @Test
    fun add_newEntity_success() {
        val dao = TestDAO(store!!)
        val entity = TestEntity(
            id = 0,
            testType = TestType.ACUITY,
            relationId = 10,
            timestamp = 10_000
        )

        val id = dao.add(entity)
        val newEntity = dao.get(id)

        assertNotNull(newEntity)
        assertEquals(entity.id, newEntity?.id)
        assertEquals(entity.testType, newEntity?.testType)
        assertEquals(entity.relationId, newEntity?.relationId)
        assertEquals(entity.timestamp, newEntity?.timestamp)
    }

    @Test
    fun add_existingEntity_overwrite() {
        val dao = TestDAO(store!!)
        val entity1 = TestEntity(
            id = 0,
            testType = TestType.ACUITY,
            relationId = 10,
            timestamp = 10_000
        )
        val entity2 = TestEntity(
            id = 0,
            testType = TestType.NEAR_FAR,
            relationId = 20,
            timestamp = 20_000
        )

        val id = dao.add(entity1)
        dao.add(entity2.copy(id = id))
        val newEntity = dao.get(id)

        assertNotNull(newEntity)
        assertEquals(id, newEntity?.id)
        assertEquals(entity2.testType, newEntity?.testType)
        assertEquals(entity2.relationId, newEntity?.relationId)
        assertEquals(entity2.timestamp, newEntity?.timestamp)
    }

    @Test
    fun get_badId_null() {
        val dao = TestDAO(store!!)
        val entity = TestEntity(
            id = 0,
            testType = TestType.ACUITY,
            relationId = 10,
            timestamp = 10_000
        )

        val id = dao.add(entity)
        val newEntity = dao.get(id + 10)

        assertNull(newEntity)
    }

    @Test
    fun delete_correctId_success() {
        val dao = TestDAO(store!!)
        val entity = TestEntity(
            id = 0,
            testType = TestType.ACUITY,
            relationId = 10,
            timestamp = 10_000
        )

        val id = dao.add(entity)
        val result = dao.delete(id)
        val newEntity = dao.get(id)

        assertTrue(result)
        assertNull(newEntity)
    }

    @Test
    fun delete_badId_noError() {
        val dao = TestDAO(store!!)
        val entity = TestEntity(
            id = 0,
            testType = TestType.ACUITY,
            relationId = 10,
            timestamp = 10_000
        )

        val id = dao.add(entity)
        val result = dao.delete(id + 10)

        assertFalse(result)
    }

    @Test
    fun deleteAll_noData_success() {
        val dao = TestDAO(store!!)

        dao.deleteAll()

        assertDoesNotThrow {
            dao.deleteAll()
        }
    }

    @Test
    fun deleteAll_withData_success() {
        val dao = TestDAO(store!!)
        val entity1 = TestEntity(
            id = 0,
            testType = TestType.ACUITY,
            relationId = 10,
            timestamp = 10_000
        )
        val entity2 = TestEntity(
            id = 0,
            testType = TestType.NEAR_FAR,
            relationId = 20,
            timestamp = 20_000
        )

        assertDoesNotThrow {
            val id1 = dao.add(entity1)
            val id2 = dao.add(entity2)
            dao.deleteAll()
            val result = listOfNotNull(
                dao.get(id1),
                dao.get(id2)
            )

            assertTrue(result.isEmpty())
        }
    }

    @Test
    fun getList() {
        // TODO
    }

//    @Test
//    fun getList_empty_success() = runTest {
//        val repository = createRepository()
//        val parameters = TestResultPagingParameters(
//            offset = 0,
//            limit = Int.MAX_VALUE,
//            filter = TestResultFilter(
//                filterByDate = false,
//                filterByType = false,
//                dateFrom = 0,
//                dateTo = System.currentTimeMillis(),
//                selectedTestTypes = mutableListOf()
//            )
//        )
//
//        val tests = repository.getList(parameters)
//
//        assertTrue(tests.isEmpty())
//    }
//
//    @Test
//    fun getList_withData_success() = runTest {
//        val repository = createRepository()
//        val generator = DevGatewayImpl(repository)
//        generator.generateData(DataGenerationType.GOOD_VISION)
//        val parameters = TestResultPagingParameters(
//            offset = 0,
//            limit = Int.MAX_VALUE,
//            filter = TestResultFilter(
//                filterByDate = false,
//                filterByType = false,
//                dateFrom = 0,
//                dateTo = System.currentTimeMillis(),
//                selectedTestTypes = mutableListOf()
//            )
//        )
//
//        val tests = repository.getList(parameters)
//
//        assertEquals(GlobalConstants.ANALYSIS_MAX_RANGE_DAYS, tests.size)
//    }
//
//    @Test
//    fun getList_disorderedData_returnOrderedByTimestampDesc() = runTest {
//        val repository = createRepository()
//        repository.add(
//            AcuityTestResult(
//                timestamp = 10_000
//            )
//        )
//        repository.add(
//            AcuityTestResult(
//                timestamp = 30_000
//            )
//        )
//        repository.add(
//            AcuityTestResult(
//                timestamp = 20_000
//            )
//        )
//        val parameters = TestResultPagingParameters(
//            offset = 0,
//            limit = Int.MAX_VALUE,
//            filter = TestResultFilter(
//                filterByDate = false,
//                filterByType = false,
//                dateFrom = 0,
//                dateTo = System.currentTimeMillis(),
//                selectedTestTypes = mutableListOf()
//            )
//        )
//
//        val tests = repository.getList(parameters)
//
//        assertEquals(3, tests.size)
//        assertTrue(tests[0].timestamp > tests[1].timestamp)
//        assertTrue(tests[1].timestamp > tests[2].timestamp)
//    }
//
//    @Test
//    fun getList_filterByDate_success() = runTest {
//        val repository = createRepository()
//        val generator = DevGatewayImpl(repository)
//        generator.generateData(DataGenerationType.GOOD_VISION)
//        val parameters = TestResultPagingParameters(
//            offset = 0,
//            limit = Int.MAX_VALUE,
//            filter = TestResultFilter(
//                filterByDate = true,
//                filterByType = false,
//                dateFrom = System.currentTimeMillis() - 10 * GlobalConstants.DAY_MS,
//                dateTo = System.currentTimeMillis(),
//                selectedTestTypes = mutableListOf()
//            )
//        )
//
//        val tests = repository.getList(parameters)
//
//        assertTrue(tests.isNotEmpty())
//        assertTrue(tests.minOf { it.timestamp } >= (parameters.filter?.dateFrom ?: 0))
//    }
//
//    @Test
//    fun getList_filterByType_success() = runTest {
//        val repository = createRepository()
//        val generator = DevGatewayImpl(repository)
//        generator.generateData(DataGenerationType.GOOD_VISION)
//        generator.generateData(DataGenerationType.OTHER_TESTS)
//        val parameters = TestResultPagingParameters(
//            offset = 0,
//            limit = Int.MAX_VALUE,
//            filter = TestResultFilter(
//                filterByDate = false,
//                filterByType = true,
//                dateFrom = 0,
//                dateTo = System.currentTimeMillis(),
//                selectedTestTypes = mutableListOf(TestType.CONTRAST)
//            )
//        )
//
//        val tests = repository.getList(parameters)
//
//        assertEquals(1, tests.size)
//        assertTrue(tests.first() is ContrastTestResult)
//    }
//
//    @Test
//    fun getList_allFilters_success() = runTest {
//        val repository = createRepository()
//        val generator = DevGatewayImpl(repository)
//        generator.generateData(DataGenerationType.GOOD_VISION)
//        generator.generateData(DataGenerationType.OTHER_TESTS)
//        val parameters = TestResultPagingParameters(
//            offset = 0,
//            limit = Int.MAX_VALUE,
//            filter = TestResultFilter(
//                filterByDate = true,
//                filterByType = true,
//                dateFrom = System.currentTimeMillis() - 10 * GlobalConstants.DAY_MS,
//                dateTo = System.currentTimeMillis(),
//                selectedTestTypes = mutableListOf(TestType.ACUITY)
//            )
//        )
//
//        val tests = repository.getList(parameters)
//
//        assertTrue(tests.isNotEmpty())
//        assertTrue(tests.minOf { it.timestamp } >= (parameters.filter?.dateFrom ?: 0))
//        assertTrue(tests.all { it is AcuityTestResult })
//    }

    @Test
    fun getListDistinctByType() {
        // TODO
    }

//    @Test
//    fun getListDistinctByType_empty_success() = runTest {
//        val repository = createRepository()
//
//        val tests = repository.getListDistinctByType()
//
//        assertTrue(tests.isEmpty())
//    }
//
//    @Test
//    fun getListDistinctByType_acuityOnly_success() = runTest {
//        val repository = createRepository()
//        val generator = DevGatewayImpl(repository)
//        generator.generateData(DataGenerationType.GOOD_VISION)
//
//        val tests = repository.getListDistinctByType()
//
//        assertEquals(1, tests.size)
//        assertTrue(tests.first() is AcuityTestResult)
//    }
//
//    @Test
//    fun getListDistinctByType_allTests_success() = runTest {
//        val repository = createRepository()
//        val generator = DevGatewayImpl(repository)
//        generator.generateData(DataGenerationType.GOOD_VISION)
//        generator.generateData(DataGenerationType.OTHER_TESTS)
//
//        val tests = repository.getListDistinctByType()
//        val testsDistinctByType = tests.distinctBy { it.javaClass }
//
//        assertEquals(TestType.entries.size, tests.size)
//        assertEquals(tests.size, testsDistinctByType.size)
//    }

    @Test
    fun getFirstNewerById() {
        // TODO
    }

    @Test
    fun getAllNewerSimilar() {
        // TODO
    }
}