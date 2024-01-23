package ru.rznnike.eyehealthmanager.app.pagination

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.clearInvocations
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.jupiter.MockitoExtension

private const val TEST_PAGE_LIMIT = 20

@ExtendWith(MockitoExtension::class)
class PaginatorTest {
    @Mock
    private lateinit var mockViewController: Paginator.ViewController<Int>

    @Test
    fun getData_empty_success() = runTest {
        val loader = FakeLoader(IntRange(1000, 1029).toList())
        val paginator = createPaginator(loader)

        val result = paginator.getData()

        assertTrue(result.isEmpty())
    }

    @Test
    fun getData_notEmpty_success() = runTest {
        val loader = FakeLoader(IntRange(1000, 1029).toList())
        val paginator = createPaginator(loader)

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        val result = paginator.getData()

        assertEquals(TEST_PAGE_LIMIT, result.size)
    }

    @Test
    fun getData_afterRefresh_updatesAllData() = runTest {
        val loader = FakeLoader(IntRange(1000, 1029).toList())
        val paginator = createPaginator(loader)

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        val beforeRefresh = paginator.getData()
        paginator.refresh()
        testScheduler.advanceUntilIdle()
        val afterRefresh = paginator.getData()

        assertEquals(loader.sourceData.size, beforeRefresh.size)
        assertEquals(loader.sourceData.size, afterRefresh.size)
    }

    @Test
    fun loadNextPage_firstPageEmpty_properViewSetup() = runTest {
        val loader = FakeLoader(emptyList())
        val paginator = createPaginator(loader)

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()

        verify(mockViewController).showProgress(
            show = true,
            isRefresh = false,
            isDataEmpty = true
        )
        verify(mockViewController).showError(
            show = false,
            error = null,
            isDataEmpty = true
        )
        verify(mockViewController).showData(emptyList())
        verify(mockViewController).showProgress(
            show = false,
            isRefresh = false,
            isDataEmpty = true
        )
        verifyNoMoreInteractions(mockViewController)
    }

    @Test
    fun loadNextPage_firstPageEmpty_stopsLoading() = runTest {
        val loader = FakeLoader(emptyList())
        val paginator = createPaginator(loader)

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        clearInvocations(mockViewController)
        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()

        verifyNoInteractions(mockViewController)
    }

    @Test
    fun loadNextPage_firstPageSmall_stopsLoading() = runTest {
        val loader = FakeLoader(IntRange(1000, 1009).toList())
        val paginator = createPaginator(loader)

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        clearInvocations(mockViewController)
        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        val result = paginator.getData()

        assertEquals(loader.sourceData.size, result.size)
        verifyNoInteractions(mockViewController)
    }

    @Test
    fun loadNextPage_firstPageBig_loadsSecondPage() = runTest {
        val loader = FakeLoader(IntRange(1000, 1029).toList())
        val paginator = createPaginator(loader)

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        clearInvocations(mockViewController)
        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        val result = paginator.getData()

        assertEquals(loader.sourceData.size, result.size)
        verify(mockViewController).showProgress(
            show = true,
            isRefresh = false,
            isDataEmpty = false
        )
        verify(mockViewController).showError(
            show = false,
            error = null,
            isDataEmpty = false
        )
        verify(mockViewController).showData(result)
        verify(mockViewController).showProgress(
            show = false,
            isRefresh = false,
            isDataEmpty = false
        )
        verifyNoMoreInteractions(mockViewController)
    }

    @Test
    fun loadNextPage_secondPageSmall_stopsLoading() = runTest {
        val loader = FakeLoader(IntRange(1000, 1029).toList())
        val paginator = createPaginator(loader)

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        clearInvocations(mockViewController)
        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        val result = paginator.getData()

        assertEquals(loader.sourceData.size, result.size)
        verifyNoInteractions(mockViewController)
    }

    @Test
    fun refresh_noData_loadsFirstPage() = runTest {
        val loader = FakeLoader(IntRange(1000, 1029).toList())
        val paginator = createPaginator(loader)

        paginator.refresh()
        testScheduler.advanceUntilIdle()
        val result = paginator.getData()

        assertEquals(TEST_PAGE_LIMIT, result.size)
    }

    @Test
    fun refresh_smallData_loadsFirstPageWithDefaultSize() = runTest {
        val loader = FakeLoader(IntRange(1000, 1009).toList())
        val paginator = createPaginator(loader)

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        loader.sourceData = IntRange(1000, 1029).toList()
        paginator.refresh()
        testScheduler.advanceUntilIdle()
        val result = paginator.getData()

        assertEquals(TEST_PAGE_LIMIT, result.size)
    }

    @Test
    fun refresh_bigData_loadsWithCurrentDataSize() = runTest {
        val loader = FakeLoader(IntRange(1000, 1129).toList())
        val paginator = createPaginator(loader)

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        paginator.refresh()
        testScheduler.advanceUntilIdle()
        val result = paginator.getData()

        assertEquals(TEST_PAGE_LIMIT * 2, result.size)
    }

    @Test
    fun refresh_newDataIsEmpty_success() = runTest {
        val loader = FakeLoader(IntRange(1000, 1029).toList())
        val paginator = createPaginator(loader)

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        loader.sourceData = emptyList()
        paginator.refresh()
        testScheduler.advanceUntilIdle()
        val result = paginator.getData()

        assertTrue(result.isEmpty())
    }

    @Test
    fun refresh_newDataIsSmaller_success() = runTest {
        val loader = FakeLoader(IntRange(1000, 1029).toList())
        val paginator = createPaginator(loader)

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        loader.sourceData = IntRange(1000, 1009).toList()
        paginator.refresh()
        testScheduler.advanceUntilIdle()
        val result = paginator.getData()

        assertEquals(loader.sourceData.size, result.size)
    }

    private class FakeLoader(
        var sourceData: List<Int>
    ) {
        @Suppress("RedundantSuspendModifier")
        suspend fun loadNext(offset: Int, limit: Int): List<Int> {
            val lastIndex = (offset + limit).coerceAtMost(sourceData.size)
            return sourceData.subList(offset, lastIndex)
        }
    }

    private fun CoroutineScope.createPaginator(loader: FakeLoader) = Paginator(
        coroutineScope = this,
        pageRequest = loader::loadNext,
        viewController = mockViewController,
        limit = TEST_PAGE_LIMIT
    )
}