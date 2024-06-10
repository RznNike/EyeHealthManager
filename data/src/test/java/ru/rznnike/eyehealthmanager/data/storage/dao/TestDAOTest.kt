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
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultFilter
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultPagingParameters
import ru.rznnike.eyehealthmanager.domain.model.test.TestType
import ru.rznnike.eyehealthmanager.domain.utils.currentTimeMillis

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
    fun getList_empty_success() {
        val dao = TestDAO(store!!)
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        val tests = dao.getList(parameters)

        assertTrue(tests.isEmpty())
    }

    @Test
    fun getList_withData_success() {
        val dao = TestDAO(store!!)
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.ACUITY,
                relationId = 10,
                timestamp = 10_000
            )
        )
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.ACUITY,
                relationId = 20,
                timestamp = 20_000
            )
        )
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.ACUITY,
                relationId = 30,
                timestamp = 30_000
            )
        )
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        val tests = dao.getList(parameters)

        assertEquals(3, tests.size)
    }

    @Test
    fun getList_disorderedData_returnOrderedByTimestampDesc() {
        val dao = TestDAO(store!!)
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.ACUITY,
                relationId = 10,
                timestamp = 10_000
            )
        )
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.ACUITY,
                relationId = 30,
                timestamp = 30_000
            )
        )
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.ACUITY,
                relationId = 20,
                timestamp = 20_000
            )
        )
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        val tests = dao.getList(parameters)

        assertEquals(3, tests.size)
        assertTrue(tests[0].timestamp > tests[1].timestamp)
        assertTrue(tests[1].timestamp > tests[2].timestamp)
    }

    @Test
    fun getList_filterByDate_success() {
        val dao = TestDAO(store!!)
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.ACUITY,
                relationId = 10,
                timestamp = 10_000
            )
        )
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.ACUITY,
                relationId = 20,
                timestamp = 20_000
            )
        )
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.ACUITY,
                relationId = 30,
                timestamp = 30_000
            )
        )
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = true,
                filterByType = false,
                dateFrom = 0,
                dateTo = 20_000,
                selectedTestTypes = mutableListOf()
            )
        )

        val tests = dao.getList(parameters)

        assertTrue(tests.isNotEmpty())
        assertTrue(tests.maxOf { it.timestamp } <= (parameters.filter?.dateTo ?: 0))
    }

    @Test
    fun getList_filterByType_success() {
        val dao = TestDAO(store!!)
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.ACUITY,
                relationId = 10,
                timestamp = 10_000
            )
        )
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.NEAR_FAR,
                relationId = 20,
                timestamp = 20_000
            )
        )
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.CONTRAST,
                relationId = 30,
                timestamp = 30_000
            )
        )
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = true,
                dateFrom = 0,
                dateTo = currentTimeMillis(),
                selectedTestTypes = mutableListOf(TestType.CONTRAST)
            )
        )

        val tests = dao.getList(parameters)

        assertEquals(1, tests.size)
        assertEquals(TestType.CONTRAST, tests.first().testType)
    }

    @Test
    fun getList_allFilters_success() {
        val dao = TestDAO(store!!)
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.ACUITY,
                relationId = 10,
                timestamp = 10_000
            )
        )
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.NEAR_FAR,
                relationId = 20,
                timestamp = 20_000
            )
        )
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.CONTRAST,
                relationId = 30,
                timestamp = 30_000
            )
        )
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = true,
                filterByType = true,
                dateFrom = 0,
                dateTo = 20_000,
                selectedTestTypes = mutableListOf(TestType.NEAR_FAR)
            )
        )

        val tests = dao.getList(parameters)

        assertTrue(tests.isNotEmpty())
        assertTrue(tests.maxOf { it.timestamp } <= (parameters.filter?.dateTo ?: 0))
        assertEquals(TestType.NEAR_FAR, tests.first().testType)
    }

    @Test
    fun getListDistinctByType_empty_success() {
        val dao = TestDAO(store!!)

        val tests = dao.getListDistinctByType()

        assertTrue(tests.isEmpty())
    }

    @Test
    fun getListDistinctByType_acuityOnly_success() {
        val dao = TestDAO(store!!)
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.ACUITY,
                relationId = 10,
                timestamp = 10_000
            )
        )
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.ACUITY,
                relationId = 20,
                timestamp = 20_000
            )
        )
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.ACUITY,
                relationId = 30,
                timestamp = 30_000
            )
        )

        val tests = dao.getListDistinctByType()

        assertEquals(1, tests.size)
        assertEquals(TestType.ACUITY, tests.first().testType)
    }

    @Test
    fun getListDistinctByType_allTests_success() {
        val dao = TestDAO(store!!)
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.ACUITY,
                relationId = 10,
                timestamp = 10_000
            )
        )
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.ASTIGMATISM,
                relationId = 20,
                timestamp = 20_000
            )
        )
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.NEAR_FAR,
                relationId = 30,
                timestamp = 30_000
            )
        )
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.COLOR_PERCEPTION,
                relationId = 40,
                timestamp = 40_000
            )
        )
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.DALTONISM,
                relationId = 50,
                timestamp = 50_000
            )
        )
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.CONTRAST,
                relationId = 60,
                timestamp = 60_000
            )
        )

        val tests = dao.getListDistinctByType()
        val testsDistinctByType = tests.distinctBy { it.testType }

        assertEquals(TestType.entries.size, tests.size)
        assertEquals(tests.size, testsDistinctByType.size)
    }

    @Test
    fun getFirstNewerById_emptyList_null() {
        val dao = TestDAO(store!!)

        val result = dao.getFirstNewerById(10)

        assertNull(result)
    }

    @Test
    fun getFirstNewerById_lastItem_null() {
        val dao = TestDAO(store!!)
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.ACUITY,
                relationId = 10,
                timestamp = 10_000
            )
        )
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.ASTIGMATISM,
                relationId = 20,
                timestamp = 20_000
            )
        )
        val id = dao.add(
            TestEntity(
                id = 0,
                testType = TestType.NEAR_FAR,
                relationId = 30,
                timestamp = 30_000
            )
        )

        val result = dao.getFirstNewerById(id)

        assertNull(result)
    }

    @Test
    fun getFirstNewerById_notLastItem_success() {
        val dao = TestDAO(store!!)
        dao.add(
            TestEntity(
                id = 0,
                testType = TestType.ACUITY,
                relationId = 10,
                timestamp = 10_000
            )
        )
        val id1 = dao.add(
            TestEntity(
                id = 0,
                testType = TestType.ASTIGMATISM,
                relationId = 20,
                timestamp = 20_000
            )
        )
        val id2 = dao.add(
            TestEntity(
                id = 0,
                testType = TestType.NEAR_FAR,
                relationId = 30,
                timestamp = 30_000
            )
        )

        val result = dao.getFirstNewerById(id1)

        assertNotNull(result)
        assertEquals(id2, result?.id)
        assertEquals(TestType.NEAR_FAR, result?.testType)
        assertEquals(30, result?.relationId)
        assertEquals(30_000, result?.timestamp)
    }

    @Test
    fun getAllNewerSimilar_emptyList_noResults() {
        val dao = TestDAO(store!!)
        val entity = TestEntity(
            id = 0,
            testType = TestType.ACUITY,
            relationId = 10,
            timestamp = 10_000
        )

        val result = dao.getAllNewerSimilar(entity)

        assertTrue(result.isEmpty())
    }

    @Test
    fun getAllNewerSimilar_noNewerSimilar_noResults() {
        val dao = TestDAO(store!!)
        val entity = TestEntity(
            id = 0,
            testType = TestType.ACUITY,
            relationId = 10,
            timestamp = 10_000
        )
        dao.add(entity)
        val id = dao.add(entity)

        val actualEntity = dao.get(id)
        val result = actualEntity?.let { dao.getAllNewerSimilar(actualEntity) } ?: emptyList()

        assertNotNull(actualEntity)
        assertTrue(result.isEmpty())
    }

    @Test
    fun getAllNewerSimilar_hasFittingData_success() {
        val dao = TestDAO(store!!)
        val entity = TestEntity(
            id = 0,
            testType = TestType.ACUITY,
            relationId = 10,
            timestamp = 10_000
        )
        val id1 = dao.add(entity)
        val id2 = dao.add(entity.copy(id = 0))

        val actualEntity = dao.get(id1)
        val actualEntity2 = dao.get(id2)
        val result = actualEntity?.let { dao.getAllNewerSimilar(actualEntity) } ?: emptyList()

        assertNotNull(actualEntity)
        assertNotNull(actualEntity2)
        assertEquals(1, result.size)
        assertEquals(id2, result.first().id)
    }
}