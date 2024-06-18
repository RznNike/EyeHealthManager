package ru.rznnike.eyehealthmanager.app.presentation.journal.backup

import android.content.ContentResolver
import android.content.Context
import android.content.UriPermission
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import org.koin.test.junit5.mock.MockProviderExtension
import org.koin.test.mock.declare
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import ru.rznnike.eyehealthmanager.app.dispatcher.event.AppEvent
import ru.rznnike.eyehealthmanager.app.dispatcher.event.EventDispatcher
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.app.utils.JournalBackupManagerAndroid
import ru.rznnike.eyehealthmanager.data.utils.DataConstants
import ru.rznnike.eyehealthmanager.domain.global.interactor.UseCaseResult
import ru.rznnike.eyehealthmanager.domain.interactor.test.ExportJournalUseCase
import ru.rznnike.eyehealthmanager.domain.model.test.TestType
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.TimeZone

@ExtendWith(MockitoExtension::class)
class ExportJournalPresenterTest : KoinTest {
    @Mock
    private lateinit var mockView: ExportJournalView
    @Mock
    private lateinit var mockContentResolver: ContentResolver
    @Mock
    private lateinit var mockUriPermission: UriPermission
    @Mock
    private lateinit var mockUri: Uri

    private val mockErrorHandler: ErrorHandler by inject()
    private val mockNotifier: Notifier by inject()
    private val mockEventDispatcher: EventDispatcher by inject()
    private val mockContext: Context by inject()
    private val mockJournalBackupManagerAndroid: JournalBackupManagerAndroid by inject()
    private val mockExportJournalUseCase: ExportJournalUseCase by inject()

    private val testDispatcher = StandardTestDispatcher()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single { Clock.systemUTC() }
                single { mock<ErrorHandler>() }
                single { mock<Notifier>() }
                single { mock<EventDispatcher>() }
                single { mock<Context>() }
                single { mock<JournalBackupManagerAndroid>() }
                single { mock<ExportJournalUseCase>() }
            }
        )
    }

    @JvmField
    @RegisterExtension
    val mockProvider = MockProviderExtension.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun beforeEach() {
        Dispatchers.setMain(testDispatcher)
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun afterEach() {
        Dispatchers.resetMain()
    }

    @Test
    fun onFirstViewAttach_clearFilters() {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-18T05:00:00Z"), ZoneOffset.UTC
            )
        }
        whenever(mockContext.contentResolver).doReturn(mockContentResolver)
        whenever(mockContentResolver.persistedUriPermissions).doReturn(listOf(mockUriPermission))
        whenever(mockUriPermission.uri).doReturn(mockUri)
        val testUrl = "test"
        whenever(mockUri.lastPathSegment).doReturn(testUrl)
        val presenter = ExportJournalPresenter()

        presenter.attachView(mockView)

        verify(mockContext).contentResolver
        verify(mockContentResolver).persistedUriPermissions
        verify(mockUriPermission).uri
        verify(mockView).populateData(
            filter = argThat { filter ->
                (!filter.filterByDate)
                        && (!filter.filterByType)
                        && (filter.dateFrom == 1702857600000L)
                        && (filter.dateTo == 1705622399999L)
                        && (filter.selectedTestTypes.isEmpty())
            },
            folderPath = eq("${testUrl}/${DataConstants.APP_DIR}/${DataConstants.EXPORT_DIR}")
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun clearFilters_populateData() {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-18T05:00:00Z"), ZoneOffset.UTC
            )
        }
        whenever(mockContext.contentResolver).doReturn(mockContentResolver)
        whenever(mockContentResolver.persistedUriPermissions).doReturn(listOf(mockUriPermission))
        whenever(mockUriPermission.uri).doReturn(mockUri)
        val testUrl = "test"
        whenever(mockUri.lastPathSegment).doReturn(testUrl)
        val presenter = ExportJournalPresenter()
        presenter.attachView(mockView)
        presenter.onFilterByDateValueChanged(true)
        clearInvocationsForAll()

        presenter.clearFilters()

        verify(mockContext).contentResolver
        verify(mockContentResolver).persistedUriPermissions
        verify(mockUriPermission).uri
        verify(mockView).populateData(
            filter = argThat { filter ->
                (!filter.filterByDate)
                        && (!filter.filterByType)
                        && (filter.dateFrom == 1702857600000L)
                        && (filter.dateTo == 1705622399999L)
                        && (filter.selectedTestTypes.isEmpty())
            },
            folderPath = eq("${testUrl}/${DataConstants.APP_DIR}/${DataConstants.EXPORT_DIR}")
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onFilterTestTypeClick_add_addedAndEnabled() {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-18T05:00:00Z"), ZoneOffset.UTC
            )
        }
        whenever(mockContext.contentResolver).doReturn(mockContentResolver)
        whenever(mockContentResolver.persistedUriPermissions).doReturn(listOf(mockUriPermission))
        whenever(mockUriPermission.uri).doReturn(mockUri)
        val testUrl = "test"
        whenever(mockUri.lastPathSegment).doReturn(testUrl)
        val presenter = ExportJournalPresenter()
        presenter.attachView(mockView)
        val selectedType = TestType.DALTONISM
        clearInvocationsForAll()

        presenter.onFilterTestTypeClick(selectedType)

        verify(mockContext).contentResolver
        verify(mockContentResolver).persistedUriPermissions
        verify(mockUriPermission).uri
        verify(mockView).populateData(
            filter = argThat { filter ->
                (!filter.filterByDate)
                        && filter.filterByType
                        && (filter.dateFrom == 1702857600000L)
                        && (filter.dateTo == 1705622399999L)
                        && (filter.selectedTestTypes == listOf(selectedType))
            },
            folderPath = eq("${testUrl}/${DataConstants.APP_DIR}/${DataConstants.EXPORT_DIR}")
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onFilterTestTypeClick_remove_removedAndDisabled() {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-18T05:00:00Z"), ZoneOffset.UTC
            )
        }
        whenever(mockContext.contentResolver).doReturn(mockContentResolver)
        whenever(mockContentResolver.persistedUriPermissions).doReturn(listOf(mockUriPermission))
        whenever(mockUriPermission.uri).doReturn(mockUri)
        val testUrl = "test"
        whenever(mockUri.lastPathSegment).doReturn(testUrl)
        val presenter = ExportJournalPresenter()
        presenter.attachView(mockView)
        val selectedType = TestType.DALTONISM
        presenter.onFilterTestTypeClick(selectedType)
        clearInvocationsForAll()

        presenter.onFilterTestTypeClick(selectedType)

        verify(mockContext).contentResolver
        verify(mockContentResolver).persistedUriPermissions
        verify(mockUriPermission).uri
        verify(mockView).populateData(
            filter = argThat { filter ->
                (!filter.filterByDate)
                        && (!filter.filterByType)
                        && (filter.dateFrom == 1702857600000L)
                        && (filter.dateTo == 1705622399999L)
                        && (filter.selectedTestTypes.isEmpty())
            },
            folderPath = eq("${testUrl}/${DataConstants.APP_DIR}/${DataConstants.EXPORT_DIR}")
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onFilterByDateValueChanged_success() {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-18T05:00:00Z"), ZoneOffset.UTC
            )
        }
        whenever(mockContext.contentResolver).doReturn(mockContentResolver)
        whenever(mockContentResolver.persistedUriPermissions).doReturn(listOf(mockUriPermission))
        whenever(mockUriPermission.uri).doReturn(mockUri)
        val testUrl = "test"
        whenever(mockUri.lastPathSegment).doReturn(testUrl)
        val presenter = ExportJournalPresenter()
        presenter.attachView(mockView)
        val filterByDate = true
        clearInvocationsForAll()

        presenter.onFilterByDateValueChanged(filterByDate)

        verify(mockContext).contentResolver
        verify(mockContentResolver).persistedUriPermissions
        verify(mockUriPermission).uri
        verify(mockView).populateData(
            filter = argThat { filter ->
                (filter.filterByDate == filterByDate)
                        && (!filter.filterByType)
                        && (filter.dateFrom == 1702857600000L)
                        && (filter.dateTo == 1705622399999L)
                        && (filter.selectedTestTypes.isEmpty())
            },
            folderPath = eq("${testUrl}/${DataConstants.APP_DIR}/${DataConstants.EXPORT_DIR}")
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onFilterByTypeValueChanged_success() {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-18T05:00:00Z"), ZoneOffset.UTC
            )
        }
        whenever(mockContext.contentResolver).doReturn(mockContentResolver)
        whenever(mockContentResolver.persistedUriPermissions).doReturn(listOf(mockUriPermission))
        whenever(mockUriPermission.uri).doReturn(mockUri)
        val testUrl = "test"
        whenever(mockUri.lastPathSegment).doReturn(testUrl)
        val presenter = ExportJournalPresenter()
        presenter.attachView(mockView)
        val filterByType = true
        clearInvocationsForAll()

        presenter.onFilterByTypeValueChanged(filterByType)

        verify(mockContext).contentResolver
        verify(mockContentResolver).persistedUriPermissions
        verify(mockUriPermission).uri
        verify(mockView).populateData(
            filter = argThat { filter ->
                (!filter.filterByDate)
                        && (filter.filterByType == filterByType)
                        && (filter.dateFrom == 1702857600000L)
                        && (filter.dateTo == 1705622399999L)
                        && (filter.selectedTestTypes.isEmpty())
            },
            folderPath = eq("${testUrl}/${DataConstants.APP_DIR}/${DataConstants.EXPORT_DIR}")
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onFilterDateFromSelected_normal_changedAndEnabled() {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-18T05:00:00Z"), ZoneOffset.UTC
            )
        }
        whenever(mockContext.contentResolver).doReturn(mockContentResolver)
        whenever(mockContentResolver.persistedUriPermissions).doReturn(listOf(mockUriPermission))
        whenever(mockUriPermission.uri).doReturn(mockUri)
        val testUrl = "test"
        whenever(mockUri.lastPathSegment).doReturn(testUrl)
        val presenter = ExportJournalPresenter()
        presenter.attachView(mockView)
        val dateFrom = 1704862193000L
        clearInvocationsForAll()

        presenter.onFilterDateFromSelected(dateFrom)

        verify(mockContext).contentResolver
        verify(mockContentResolver).persistedUriPermissions
        verify(mockUriPermission).uri
        verify(mockView).populateData(
            filter = argThat { filter ->
                (filter.filterByDate)
                        && (!filter.filterByType)
                        && (filter.dateFrom == 1704844800000L)
                        && (filter.dateTo == 1705622399999L)
                        && (filter.selectedTestTypes.isEmpty())
            },
            folderPath = eq("${testUrl}/${DataConstants.APP_DIR}/${DataConstants.EXPORT_DIR}")
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onFilterDateFromSelected_largerThanDateTo_changedAndEnabled() {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-18T05:00:00Z"), ZoneOffset.UTC
            )
        }
        whenever(mockContext.contentResolver).doReturn(mockContentResolver)
        whenever(mockContentResolver.persistedUriPermissions).doReturn(listOf(mockUriPermission))
        whenever(mockUriPermission.uri).doReturn(mockUri)
        val testUrl = "test"
        whenever(mockUri.lastPathSegment).doReturn(testUrl)
        val presenter = ExportJournalPresenter()
        presenter.attachView(mockView)
        val dateFrom = 1705716000000L
        clearInvocationsForAll()

        presenter.onFilterDateFromSelected(dateFrom)

        verify(mockContext).contentResolver
        verify(mockContentResolver).persistedUriPermissions
        verify(mockUriPermission).uri
        verify(mockView).populateData(
            filter = argThat { filter ->
                (filter.filterByDate)
                        && (!filter.filterByType)
                        && (filter.dateFrom == 1705708800000L)
                        && (filter.dateTo == 1705795199999L)
                        && (filter.selectedTestTypes.isEmpty())
            },
            folderPath = eq("${testUrl}/${DataConstants.APP_DIR}/${DataConstants.EXPORT_DIR}")
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onFilterDateToSelected_normal_changedAndEnabled() {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-18T05:00:00Z"), ZoneOffset.UTC
            )
        }
        whenever(mockContext.contentResolver).doReturn(mockContentResolver)
        whenever(mockContentResolver.persistedUriPermissions).doReturn(listOf(mockUriPermission))
        whenever(mockUriPermission.uri).doReturn(mockUri)
        val testUrl = "test"
        whenever(mockUri.lastPathSegment).doReturn(testUrl)
        val presenter = ExportJournalPresenter()
        presenter.attachView(mockView)
        val dateTo = 1705284000000L
        clearInvocationsForAll()

        presenter.onFilterDateToSelected(dateTo)

        verify(mockContext).contentResolver
        verify(mockContentResolver).persistedUriPermissions
        verify(mockUriPermission).uri
        verify(mockView).populateData(
            filter = argThat { filter ->
                (filter.filterByDate)
                        && (!filter.filterByType)
                        && (filter.dateFrom == 1702857600000L)
                        && (filter.dateTo == 1705363199999L)
                        && (filter.selectedTestTypes.isEmpty())
            },
            folderPath = eq("${testUrl}/${DataConstants.APP_DIR}/${DataConstants.EXPORT_DIR}")
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun onFilterDateToSelected_smallerThanDateFrom_changedAndEnabled() {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-18T05:00:00Z"), ZoneOffset.UTC
            )
        }
        whenever(mockContext.contentResolver).doReturn(mockContentResolver)
        whenever(mockContentResolver.persistedUriPermissions).doReturn(listOf(mockUriPermission))
        whenever(mockUriPermission.uri).doReturn(mockUri)
        val testUrl = "test"
        whenever(mockUri.lastPathSegment).doReturn(testUrl)
        val presenter = ExportJournalPresenter()
        presenter.attachView(mockView)
        val dateTo = 1701857600000L
        clearInvocationsForAll()

        presenter.onFilterDateToSelected(dateTo)

        verify(mockContext).contentResolver
        verify(mockContentResolver).persistedUriPermissions
        verify(mockUriPermission).uri
        verify(mockView).populateData(
            filter = argThat { filter ->
                (filter.filterByDate)
                        && (!filter.filterByType)
                        && (filter.dateFrom == 1701820800000L)
                        && (filter.dateTo == 1701907199999L)
                        && (filter.selectedTestTypes.isEmpty())
            },
            folderPath = eq("${testUrl}/${DataConstants.APP_DIR}/${DataConstants.EXPORT_DIR}")
        )
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun startExport_folderSelectedAndSuccess_closeScreen() = runTest {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-18T05:00:00Z"), ZoneOffset.UTC
            )
        }
        whenever(mockContext.contentResolver).doReturn(mockContentResolver)
        whenever(mockContentResolver.persistedUriPermissions).doReturn(listOf(mockUriPermission))
        whenever(mockUriPermission.uri).doReturn(mockUri)
        val testUrl = "test"
        whenever(mockUri.lastPathSegment).doReturn(testUrl)
        whenever(mockExportJournalUseCase(any())).doReturn(
            UseCaseResult(ExportJournalUseCase.Result(mockUri))
        )
        val presenter = ExportJournalPresenter()
        presenter.attachView(mockView)
        clearInvocationsForAll()

        presenter.startExport()
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockJournalBackupManagerAndroid).context = mockContext
        verify(mockExportJournalUseCase)(any())
        verify(mockJournalBackupManagerAndroid).context = null
        verify(mockEventDispatcher).sendEvent(
            argThat {
                (this is AppEvent.JournalExported)
                        && (uri == mockUri)
            }
        )
        verify(mockView).routerExit()
        verify(mockView).setProgress(show = false, immediately = true)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun startExport_folderSelectedAndException_errorHandler() = runTest {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-18T05:00:00Z"), ZoneOffset.UTC
            )
        }
        whenever(mockContext.contentResolver).doReturn(mockContentResolver)
        whenever(mockContentResolver.persistedUriPermissions).doReturn(listOf(mockUriPermission))
        whenever(mockUriPermission.uri).doReturn(mockUri)
        val testUrl = "test"
        whenever(mockUri.lastPathSegment).doReturn(testUrl)
        whenever(mockExportJournalUseCase(any())).doReturn(UseCaseResult(error = Exception()))
        val presenter = ExportJournalPresenter()
        presenter.attachView(mockView)
        clearInvocationsForAll()

        presenter.startExport()
        testScheduler.advanceUntilIdle()

        verify(mockView).setProgress(show = true, immediately = true)
        verify(mockJournalBackupManagerAndroid).context = mockContext
        verify(mockExportJournalUseCase)(any())
        verify(mockErrorHandler).proceed(any(), any())
        verify(mockJournalBackupManagerAndroid).context = null
        verify(mockView).setProgress(show = false, immediately = true)
        verifyNoMoreInteractionsForAll()
    }

    @Test
    fun startExport_folderNotSelected_openSelector() {
        declare {
            Clock.fixed(
                Instant.parse( "2024-01-18T05:00:00Z"), ZoneOffset.UTC
            )
        }
        whenever(mockContext.contentResolver).doReturn(mockContentResolver)
        whenever(mockContentResolver.persistedUriPermissions).doReturn(emptyList())
        val presenter = ExportJournalPresenter()
        presenter.attachView(mockView)
        clearInvocationsForAll()

        presenter.startExport()

        verify(mockView).selectExportFolder()
        verifyNoMoreInteractionsForAll()
    }

    private val allMocks by lazy {
        arrayOf(
            mockView,
            mockContentResolver,
            mockUriPermission,
            mockUri,
            mockErrorHandler,
            mockNotifier,
            mockEventDispatcher,
            mockContext,
            mockJournalBackupManagerAndroid,
            mockExportJournalUseCase
        )
    }

    private fun clearInvocationsForAll() = clearInvocations(*allMocks)

    private fun verifyNoMoreInteractionsForAll() = verifyNoMoreInteractions(*allMocks)
}