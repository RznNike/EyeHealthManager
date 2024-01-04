package ru.rznnike.eyehealthmanager.app.pagination

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.anyBoolean
import org.mockito.Mockito.anyList
import org.mockito.Mockito.never
import org.mockito.Mockito.reset
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

private const val TEST_PAGE_LIMIT = 20

@ExtendWith(MockitoExtension::class)
class PaginatorTest {
    @Mock
    private lateinit var mockViewController: Paginator.ViewController<Int>

    @Test
    fun getData_empty_success() = runTest {
        val sourceData = IntRange(1000, 1029).toList()
        @Suppress("RedundantSuspendModifier")
        suspend fun loadNext(offset: Int, limit: Int): List<Int> {
            val lastIndex = (offset + limit).coerceAtMost(sourceData.size)
            return sourceData.subList(offset, lastIndex)
        }
        val paginator = Paginator(
            coroutineScope = this,
            pageRequest = ::loadNext,
            viewController = mockViewController,
            limit = TEST_PAGE_LIMIT
        )

        val result = paginator.getData()

        assertTrue(result.isEmpty())
    }

    @Test
    fun getData_notEmpty_success() = runTest {
        val sourceData = IntRange(1000, 1029).toList()
        @Suppress("RedundantSuspendModifier")
        suspend fun loadNext(offset: Int, limit: Int): List<Int> {
            val lastIndex = (offset + limit).coerceAtMost(sourceData.size)
            return sourceData.subList(offset, lastIndex)
        }
        val paginator = Paginator(
            coroutineScope = this,
            pageRequest = ::loadNext,
            viewController = mockViewController,
            limit = TEST_PAGE_LIMIT
        )

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        val result = paginator.getData()

        assertEquals(TEST_PAGE_LIMIT, result.size)
    }

    @Test
    fun getData_afterRefresh_updatesAllData() = runTest {
        val sourceData = IntRange(1000, 1029).toList()
        @Suppress("RedundantSuspendModifier")
        suspend fun loadNext(offset: Int, limit: Int): List<Int> {
            val lastIndex = (offset + limit).coerceAtMost(sourceData.size)
            return sourceData.subList(offset, lastIndex)
        }
        val paginator = Paginator(
            coroutineScope = this,
            pageRequest = ::loadNext,
            viewController = mockViewController,
            limit = TEST_PAGE_LIMIT
        )

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        val beforeRefresh = paginator.getData()
        paginator.refresh()
        testScheduler.advanceUntilIdle()
        val afterRefresh = paginator.getData()

        assertEquals(sourceData.size, beforeRefresh.size)
        assertEquals(sourceData.size, afterRefresh.size)
    }

    @Test
    fun loadNextPage_firstPageEmpty_properViewSetup() = runTest {
        val sourceData = emptyList<Int>()
        @Suppress("RedundantSuspendModifier")
        suspend fun loadNext(offset: Int, limit: Int): List<Int> {
            val lastIndex = (offset + limit).coerceAtMost(sourceData.size)
            return sourceData.subList(offset, lastIndex)
        }
        val paginator = Paginator(
            coroutineScope = this,
            pageRequest = ::loadNext,
            viewController = mockViewController,
            limit = TEST_PAGE_LIMIT
        )

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()

        verify(mockViewController, times(1)).showProgress(
            show = true,
            isRefresh = false,
            isDataEmpty = true
        )
        verify(mockViewController, times(1)).showError(
            show = false,
            error = null,
            isDataEmpty = true
        )
        verify(mockViewController, times(1)).showData(emptyList())
        verify(mockViewController, times(1)).showProgress(
            show = false,
            isRefresh = false,
            isDataEmpty = true
        )
    }

    @Test
    fun loadNextPage_firstPageEmpty_stopsLoading() = runTest {
        val sourceData = emptyList<Int>()
        @Suppress("RedundantSuspendModifier")
        suspend fun loadNext(offset: Int, limit: Int): List<Int> {
            val lastIndex = (offset + limit).coerceAtMost(sourceData.size)
            return sourceData.subList(offset, lastIndex)
        }
        val paginator = Paginator(
            coroutineScope = this,
            pageRequest = ::loadNext,
            viewController = mockViewController,
            limit = TEST_PAGE_LIMIT
        )

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        reset(mockViewController)
        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()

        verify(mockViewController, never()).showProgress(
            show = anyBoolean(),
            isRefresh = anyBoolean(),
            isDataEmpty = anyBoolean()
        )
        verify(mockViewController, never()).showError(
            show = anyBoolean(),
            error = any(Throwable::class.java),
            isDataEmpty = anyBoolean()
        )
        verify(mockViewController, never()).showData(anyList())
    }

    @Test
    fun loadNextPage_firstPageSmall_stopsLoading() = runTest {
        val sourceData = IntRange(1000, 1009).toList()
        @Suppress("RedundantSuspendModifier")
        suspend fun loadNext(offset: Int, limit: Int): List<Int> {
            val lastIndex = (offset + limit).coerceAtMost(sourceData.size)
            return sourceData.subList(offset, lastIndex)
        }
        val paginator = Paginator(
            coroutineScope = this,
            pageRequest = ::loadNext,
            viewController = mockViewController,
            limit = TEST_PAGE_LIMIT
        )

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        reset(mockViewController)
        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        val result = paginator.getData()

        assertEquals(sourceData.size, result.size)
        verify(mockViewController, never()).showProgress(
            show = anyBoolean(),
            isRefresh = anyBoolean(),
            isDataEmpty = anyBoolean()
        )
        verify(mockViewController, never()).showError(
            show = anyBoolean(),
            error = any(Throwable::class.java),
            isDataEmpty = anyBoolean()
        )
        verify(mockViewController, never()).showData(anyList())
    }

    @Test
    fun loadNextPage_firstPageBig_loadsSecondPage() = runTest {
        val sourceData = IntRange(1000, 1029).toList()
        @Suppress("RedundantSuspendModifier")
        suspend fun loadNext(offset: Int, limit: Int): List<Int> {
            val lastIndex = (offset + limit).coerceAtMost(sourceData.size)
            return sourceData.subList(offset, lastIndex)
        }
        val paginator = Paginator(
            coroutineScope = this,
            pageRequest = ::loadNext,
            viewController = mockViewController,
            limit = TEST_PAGE_LIMIT
        )

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        reset(mockViewController)
        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        val result = paginator.getData()

        assertEquals(sourceData.size, result.size)
        verify(mockViewController, times(1)).showProgress(
            show = true,
            isRefresh = false,
            isDataEmpty = false
        )
        verify(mockViewController, times(1)).showError(
            show = false,
            error = null,
            isDataEmpty = false
        )
        verify(mockViewController, times(1)).showData(result)
        verify(mockViewController, times(1)).showProgress(
            show = false,
            isRefresh = false,
            isDataEmpty = false
        )
    }

    @Test
    fun loadNextPage_secondPageSmall_stopsLoading() = runTest {
        val sourceData = IntRange(1000, 1029).toList()
        @Suppress("RedundantSuspendModifier")
        suspend fun loadNext(offset: Int, limit: Int): List<Int> {
            val lastIndex = (offset + limit).coerceAtMost(sourceData.size)
            return sourceData.subList(offset, lastIndex)
        }
        val paginator = Paginator(
            coroutineScope = this,
            pageRequest = ::loadNext,
            viewController = mockViewController,
            limit = TEST_PAGE_LIMIT
        )

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        reset(mockViewController)
        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        val result = paginator.getData()

        assertEquals(sourceData.size, result.size)
        verify(mockViewController, never()).showProgress(
            show = anyBoolean(),
            isRefresh = anyBoolean(),
            isDataEmpty = anyBoolean()
        )
        verify(mockViewController, never()).showError(
            show = anyBoolean(),
            error = any(Throwable::class.java),
            isDataEmpty = anyBoolean()
        )
        verify(mockViewController, never()).showData(anyList())
    }

    @Test
    fun refresh_noData_loadsFirstPage() = runTest {
        val sourceData = IntRange(1000, 1029).toList()
        @Suppress("RedundantSuspendModifier")
        suspend fun loadNext(offset: Int, limit: Int): List<Int> {
            val lastIndex = (offset + limit).coerceAtMost(sourceData.size)
            return sourceData.subList(offset, lastIndex)
        }
        val paginator = Paginator(
            coroutineScope = this,
            pageRequest = ::loadNext,
            viewController = mockViewController,
            limit = TEST_PAGE_LIMIT
        )

        paginator.refresh()
        testScheduler.advanceUntilIdle()
        val result = paginator.getData()

        assertEquals(TEST_PAGE_LIMIT, result.size)
    }

    @Test
    fun refresh_smallData_loadsFirstPageWithDefaultSize() = runTest {
        var sourceData = IntRange(1000, 1009).toList()
        @Suppress("RedundantSuspendModifier")
        suspend fun loadNext(offset: Int, limit: Int): List<Int> {
            val lastIndex = (offset + limit).coerceAtMost(sourceData.size)
            return sourceData.subList(offset, lastIndex)
        }
        val paginator = Paginator(
            coroutineScope = this,
            pageRequest = ::loadNext,
            viewController = mockViewController,
            limit = TEST_PAGE_LIMIT
        )

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        sourceData = IntRange(1000, 1029).toList()
        paginator.refresh()
        testScheduler.advanceUntilIdle()
        val result = paginator.getData()

        assertEquals(TEST_PAGE_LIMIT, result.size)
    }

    @Test
    fun refresh_bigData_loadsWithCurrentDataSize() = runTest {
        val sourceData = IntRange(1000, 1129).toList()
        @Suppress("RedundantSuspendModifier")
        suspend fun loadNext(offset: Int, limit: Int): List<Int> {
            val lastIndex = (offset + limit).coerceAtMost(sourceData.size)
            return sourceData.subList(offset, lastIndex)
        }
        val paginator = Paginator(
            coroutineScope = this,
            pageRequest = ::loadNext,
            viewController = mockViewController,
            limit = TEST_PAGE_LIMIT
        )

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
        var sourceData = IntRange(1000, 1029).toList()
        @Suppress("RedundantSuspendModifier")
        suspend fun loadNext(offset: Int, limit: Int): List<Int> {
            val lastIndex = (offset + limit).coerceAtMost(sourceData.size)
            return sourceData.subList(offset, lastIndex)
        }
        val paginator = Paginator(
            coroutineScope = this,
            pageRequest = ::loadNext,
            viewController = mockViewController,
            limit = TEST_PAGE_LIMIT
        )

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        sourceData = emptyList()
        paginator.refresh()
        testScheduler.advanceUntilIdle()
        val result = paginator.getData()

        assertTrue(result.isEmpty())
    }

    @Test
    fun refresh_newDataIsSmaller_success() = runTest {
        var sourceData = IntRange(1000, 1029).toList()
        @Suppress("RedundantSuspendModifier")
        suspend fun loadNext(offset: Int, limit: Int): List<Int> {
            val lastIndex = (offset + limit).coerceAtMost(sourceData.size)
            return sourceData.subList(offset, lastIndex)
        }
        val paginator = Paginator(
            coroutineScope = this,
            pageRequest = ::loadNext,
            viewController = mockViewController,
            limit = TEST_PAGE_LIMIT
        )

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        sourceData = IntRange(1000, 1009).toList()
        paginator.refresh()
        testScheduler.advanceUntilIdle()
        val result = paginator.getData()

        assertEquals(sourceData.size, result.size)
    }
}