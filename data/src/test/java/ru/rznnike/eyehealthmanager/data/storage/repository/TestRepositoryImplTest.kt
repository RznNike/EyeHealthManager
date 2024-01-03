package ru.rznnike.eyehealthmanager.data.storage.repository

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.rznnike.eyehealthmanager.data.gateway.DevGatewayImpl
import ru.rznnike.eyehealthmanager.domain.model.TestResultFilter
import ru.rznnike.eyehealthmanager.domain.model.TestResultPagingParameters
import ru.rznnike.eyehealthmanager.domain.model.enums.DataGenerationType
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants

class TestRepositoryImplTest : AbstractObjectBoxTest() {
    @Test
    fun getTests_empty_success() = runTest {
        val repository = TestRepositoryImpl(store!!)
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = System.currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        val tests = repository.getTests(parameters)

        assert(tests.isEmpty())
    }

    @Test
    fun getTests_withData_success() = runTest {
        val repository = TestRepositoryImpl(store!!)
        val generator = DevGatewayImpl(repository)
        generator.generateData(DataGenerationType.GOOD_VISION)
        val parameters = TestResultPagingParameters(
            offset = 0,
            limit = Int.MAX_VALUE,
            filter = TestResultFilter(
                filterByDate = false,
                filterByType = false,
                dateFrom = 0,
                dateTo = System.currentTimeMillis(),
                selectedTestTypes = mutableListOf()
            )
        )

        val tests = repository.getTests(parameters)

        assertEquals(GlobalConstants.ANALYSIS_MAX_RANGE_DAYS, tests.size)
    }

    @Test
    fun getTests_filterByDate_success() = runTest {
        // TODO
    }

    @Test
    fun getTests_filterByType_success() = runTest {
        // TODO
    }

    @Test
    fun getTests_allFilters_success() = runTest {
        // TODO
    }

    @Test
    fun getAllLastTests_empty_success() {
        // TODO
    }

    @Test
    fun getAllLastTests_acuityOnly_success() {
        // TODO
    }

    @Test
    fun getAllLastTests_allTests_success() {
        // TODO
    }

    @Test
    fun addTests_empty_success() {
        // TODO
    }

    @Test
    fun addTests_withData_success() {
        // TODO
    }

    @Test
    fun addTest_success() {
        // TODO
    }

    @Test
    fun deleteTestById_correctId_success() {
        // TODO
    }

    @Test
    fun deleteTestById_badId_success() {
        // TODO
    }

    @Test
    fun deleteAllTests_empty_success() {
        // TODO
    }

    @Test
    fun deleteAllTests_withData_success() {
        // TODO
    }

    @Test
    fun deleteDuplicates_noDuplicates_noChanges() {
        // TODO
    }

    @Test
    fun deleteDuplicates_withDuplicates_success() {
        // TODO
    }
}