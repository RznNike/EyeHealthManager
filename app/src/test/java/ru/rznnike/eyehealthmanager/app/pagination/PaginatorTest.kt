package ru.rznnike.eyehealthmanager.app.pagination

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

private const val TEST_PAGE_LIMIT = 20

class PaginatorTest {
    @Test
    fun getData_empty_success() = runTest {
        val sourceData = IntRange(1000, 1029).toList()
        @Suppress("RedundantSuspendModifier")
        suspend fun loadNext(offset: Int, limit: Int): List<Int> {
            val lastIndex = (offset + limit).coerceAtMost(sourceData.size)
            return sourceData.subList(offset, lastIndex)
        }
        val viewController = mock(ViewController::class.java)
        val paginator = Paginator(
            coroutineScope = this,
            pageRequest = ::loadNext,
            viewController = viewController,
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
        val viewController = mock(ViewController::class.java)
        val paginator = Paginator(
            coroutineScope = this,
            pageRequest = ::loadNext,
            viewController = viewController
        )

        paginator.loadNextPage()
        testScheduler.advanceUntilIdle()
        val result = paginator.getData()

        assertEquals(TEST_PAGE_LIMIT, result.size)
    }

    @Test
    fun getData_afterRefresh_success() = runTest {
        // TODO
//        val sourceData = IntRange(1000, 1029).toList()
//        @Suppress("RedundantSuspendModifier")
//        suspend fun loadNext(offset: Int, limit: Int): List<Int> {
//            val lastIndex = (offset + limit).coerceAtMost(sourceData.size)
//            return sourceData.subList(offset, lastIndex)
//        }
//        val viewController = mock(ViewController::class.java)
//        val paginator = Paginator(
//            coroutineScope = this,
//            pageRequest = ::loadNext,
//            viewController = viewController
//        )
//
//        paginator.loadNextPage()
//        paginator.loadNextPage()
//        val beforeRefresh = paginator.getData()
//        paginator.refresh()
//        val afterRefresh = paginator.getData()
//
//        assertEquals(sourceData.size, beforeRefresh.size)
//        assertEquals(sourceData.size, afterRefresh.size)
    }

    @Test
    fun loadNextPage_firstPageEmpty_stopsLoading() = runTest {
        // TODO
//        val sourceData = emptyList<Int>()
//        @Suppress("RedundantSuspendModifier")
//        suspend fun loadNext(offset: Int, limit: Int): List<Int> {
//            val lastIndex = (offset + limit).coerceAtMost(sourceData.size)
//            return sourceData.subList(offset, lastIndex)
//        }
//        val viewController = mock(ViewController::class.java)
//        val paginator = Paginator(
//            coroutineScope = this,
//            pageRequest = ::loadNext,
//            viewController = viewController
//        )
//        Mockito.reset(viewController)
//
//        paginator.loadNextPage()
//
//        verify(viewController).showProgress(
//            show = true,
//            isRefresh = false,
//            isDataEmpty = true
//        )
//        verify(viewController).showError(
//            show = false,
//            error = null,
//            isDataEmpty = true
//        )
//        verify(viewController).showData(emptyList())
//
//        verify(viewController, never()).showData(emptyList())
    }

    @Test
    fun loadNextPage_firstPageSmall_stopsLoading() = runTest {
        // TODO
    }

    @Test
    fun loadNextPage_firstPageBig_readyToLoadMore() = runTest {
        // TODO
    }

    @Test
    fun loadNextPage_secondPageEmpty_stopsLoading() = runTest {
        // TODO
    }

    @Test
    fun loadNextPage_secondPageSmall_stopsLoading() = runTest {
        // TODO
    }

    @Test
    fun refresh_noData_loadsFirstPage() = runTest {
        // TODO
    }

    @Test
    fun refresh_smallData_loadsFirstPageWithDefaultSize() = runTest {
        // TODO
    }

    @Test
    fun refresh_bigData_loadsWithCurrentDataSize() = runTest {
        // TODO
    }

    @Test
    fun refresh_newDataIsEmpty_success() = runTest {
        // TODO
    }

    @Test
    fun refresh_newDataIsSmaller_success() = runTest {
        // TODO
    }

    private interface ViewController : Paginator.ViewController<Int>
}