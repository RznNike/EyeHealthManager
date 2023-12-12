package ru.rznnike.eyehealthmanager.app.ui.fragment.main.journal

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.listeners.OnBindViewHolderListenerImpl
import com.mikepenz.fastadapter.ui.items.ProgressItem
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogAction
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogParameters
import ru.rznnike.eyehealthmanager.app.dialog.bottom.BottomDialogAction
import ru.rznnike.eyehealthmanager.app.dialog.showAlertDialog
import ru.rznnike.eyehealthmanager.app.dialog.showBottomDialog
import ru.rznnike.eyehealthmanager.app.dialog.showCustomBottomDialog
import ru.rznnike.eyehealthmanager.app.dialog.showDatePicker
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.main.journal.JournalPresenter
import ru.rznnike.eyehealthmanager.app.presentation.main.journal.JournalView
import ru.rznnike.eyehealthmanager.app.ui.item.TestResultItem
import ru.rznnike.eyehealthmanager.app.ui.item.TestTypeSmallItem
import ru.rznnike.eyehealthmanager.app.ui.view.EmptyDividerDecoration
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.app.utils.extensions.createFastAdapter
import ru.rznnike.eyehealthmanager.app.utils.extensions.setVisible
import ru.rznnike.eyehealthmanager.databinding.BottomDialogJournalFiltersBinding
import ru.rznnike.eyehealthmanager.databinding.FragmentJournalBinding
import ru.rznnike.eyehealthmanager.domain.model.TestResult
import ru.rznnike.eyehealthmanager.domain.model.TestResultFilterParams
import ru.rznnike.eyehealthmanager.domain.model.enums.TestType
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.toDate
import java.util.*

class JournalFragment : BaseFragment(R.layout.fragment_journal), JournalView {
    @InjectPresenter
    lateinit var presenter: JournalPresenter

    private val binding by viewBinding(FragmentJournalBinding::bind)

    private lateinit var adapter: FastAdapter<IItem<*>>
    private lateinit var itemAdapter: ItemAdapter<IItem<*>>
    private lateinit var footerAdapter: ItemAdapter<ProgressItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            recyclerView.addSystemWindowInsetToPadding(top = true)
        }
        initRecyclerView()
    }

    private fun initAdapter() {
        itemAdapter = ItemAdapter()
        footerAdapter = ItemAdapter()
        adapter = createFastAdapter(itemAdapter, footerAdapter)
        adapter.setHasStableIds(true)

        val preloadItemPosition = GlobalConstants.PRELOAD_ITEM_POSITION
        adapter.onBindViewHolderListener = object : OnBindViewHolderListenerImpl<IItem<*>>() {
            override fun onBindViewHolder(
                viewHolder: RecyclerView.ViewHolder,
                position: Int,
                payloads: List<Any>
            ) {
                super.onBindViewHolder(viewHolder, position, payloads)
                if (position == adapter.itemCount - preloadItemPosition) {
                    binding.recyclerView.post { presenter.loadNext() }
                }
            }
        }
        adapter.onClickListener = { _, _, item, _ ->
            when (item) {
                is TestResultItem -> {
                    showTestResultContextMenu(item.testResult)
                    true
                }
                else -> false
            }
        }
    }

    private fun initRecyclerView() = binding.apply {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@JournalFragment.adapter
            itemAnimator = null
            addItemDecoration(
                EmptyDividerDecoration(
                    context = requireContext(),
                    cardInsets = R.dimen.baseline_grid_16
                )
            )
        }
    }

    override fun populateData(data: List<TestResult>, filterParams: TestResultFilterParams) {
        binding.apply {
            itemAdapter.setNewList(data.map { TestResultItem(it) })
            zeroView.setVisible(data.isEmpty())
            buttonActions.setOnClickListener {
                showActionsDialog(filterParams)
            }
            imageViewFilterIcon.setVisible(
                filterParams.filterByDate || filterParams.filterByType
            )
        }
    }

    override fun showProgress(show: Boolean, isRefresh: Boolean, isDataEmpty: Boolean) {
        fun setFooter(show: Boolean) {
            view?.post {
                footerAdapter.clear()
                if (show) {
                    footerAdapter.add(ProgressItem().apply { isEnabled = false })
                }
            }
        }

        when {
            isDataEmpty -> {
                binding.progressView.setProgress(show)
                setFooter(false)
            }
            isRefresh -> {
                binding.progressView.setProgress(show)
                setFooter(false)
            }
            else -> {
                binding.progressView.setProgress(false)
                setFooter(show)
            }
        }
    }

    override fun showErrorView(show: Boolean, message: String?) {
        binding.apply {
            errorView.setVisible(show)
            errorView.messageText = message ?: getString(R.string.unknown_error)
        }
    }

    private fun showTestResultContextMenu(testResult: TestResult) {
        showBottomDialog(
            header = getString(R.string.choose_action),
            actions = listOf(
                BottomDialogAction(getString(R.string.repeat_test)) {
                    it.dismiss()
                    presenter.openTestInfo(testResult)
                },
                BottomDialogAction(getString(R.string.delete_result)) {
                    it.dismiss()
                    showDeletionConfirmationDialog(testResult)
                }
            )
        )
    }

    private fun showDeletionConfirmationDialog(testResult: TestResult) {
        showAlertDialog(
            parameters = AlertDialogParameters.HORIZONTAL_2_OPTIONS_LEFT_ACCENT,
            header = getString(R.string.result_deletion_header),
            message = getString(R.string.result_deletion_message),
            actions = listOf(
                AlertDialogAction(getString(R.string.cancel)) {
                    it.cancel()
                },
                AlertDialogAction(getString(R.string.yes)) {
                    it.cancel()
                    presenter.onDeleteTestResult(testResult)
                }
            )
        )
    }

    private fun showActionsDialog(filterParams: TestResultFilterParams) {
        showBottomDialog(
            header = getString(R.string.choose_action),
            actions = listOf(
                BottomDialogAction(getString(R.string.analysis)) {
                    it.dismiss()
                    presenter.analyseData()
                },
                BottomDialogAction(getString(R.string.export)) {
                    it.dismiss()
                    presenter.exportData()
                },
                BottomDialogAction(getString(R.string.import_string)) {
                    it.dismiss()
                    presenter.importData()
                },
                BottomDialogAction(
                    text = getString(R.string.filters),
                    selected = filterParams.filterByDate || filterParams.filterByType
                ) {
                    it.dismiss()
                    showFilterDialog(filterParams)
                }
            )
        )
    }

    private fun showFilterDialog(filterParams: TestResultFilterParams) {
        BottomDialogJournalFiltersBinding.inflate(layoutInflater).apply {
            showCustomBottomDialog(
                rootView = root,
                constraintView = rootDialogLayout,
                constraintChildView = layoutDialogContent
            ) { dialog ->
                layoutDialogContent.addSystemWindowInsetToPadding(bottom = true)

                val newFilterParams = filterParams.deepCopy()

                val itemAdapterTestType: ItemAdapter<IItem<*>> = ItemAdapter()
                val adapterTestType: FastAdapter<IItem<*>> = createFastAdapter(itemAdapterTestType)
                adapterTestType.setHasStableIds(true)

                fun updateTestTypes() {
                    itemAdapterTestType.setNewList(
                        TestType.entries.map {
                            TestTypeSmallItem(
                                testType = it,
                                selection = newFilterParams.selectedTestTypes.contains(it)
                            )
                        }
                    )
                    checkBoxFilterByType.isChecked = newFilterParams.filterByType
                }

                fun onFilterTestTypeClick(testType: TestType) {
                    if (newFilterParams.selectedTestTypes.contains(testType)) {
                        newFilterParams.selectedTestTypes.remove(testType)
                    } else {
                        newFilterParams.selectedTestTypes.add(testType)
                    }
                    newFilterParams.filterByType = newFilterParams.selectedTestTypes.isNotEmpty()
                    updateTestTypes()
                }

                adapterTestType.onClickListener = { _, _, item, _ ->
                    when (item) {
                        is TestTypeSmallItem -> {
                            onFilterTestTypeClick(item.testType)
                            true
                        }
                        else -> false
                    }
                }

                recyclerViewFilterTypes.apply {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = adapterTestType
                    itemAnimator = null
                    addItemDecoration(
                        EmptyDividerDecoration(
                            context = requireContext(),
                            cardInsets = R.dimen.baseline_grid_8,
                            applyOutsideDecoration = false
                        )
                    )
                }

                updateTestTypes()

                buttonDialogClearFilters.setOnClickListener {
                    presenter.clearFilter()
                    dialog.dismiss()
                }
                checkBoxFilterByDate.setOnClickListener {
                    newFilterParams.filterByDate = checkBoxFilterByDate.isChecked
                }
                checkBoxFilterByType.setOnClickListener {
                    newFilterParams.filterByType = checkBoxFilterByType.isChecked
                }

                fun updateDates() {
                    checkBoxFilterByDate.isChecked = newFilterParams.filterByDate
                    buttonDateFrom.text = newFilterParams.dateFrom.toDate()
                    buttonDateTo.text = newFilterParams.dateTo.toDate()
                }
                updateDates()

                buttonDateFrom.setOnClickListener {
                    showDatePicker(
                        preselectedDate = newFilterParams.dateFrom
                    ) { timestamp ->
                        newFilterParams.dateFrom = timestamp
                        if (newFilterParams.dateTo <= newFilterParams.dateFrom) {
                            val calendar = Calendar.getInstance().apply {
                                timeInMillis = timestamp
                                set(Calendar.HOUR_OF_DAY, 23)
                                set(Calendar.MINUTE, 59)
                                set(Calendar.SECOND, 59)
                                set(Calendar.MILLISECOND, 999)
                            }
                            newFilterParams.dateTo = calendar.timeInMillis
                        }
                        newFilterParams.filterByDate = true
                        updateDates()
                    }
                }
                buttonDateTo.setOnClickListener {
                    showDatePicker(
                        preselectedDate = newFilterParams.dateTo
                    ) { timestamp ->
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = timestamp
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                            set(Calendar.SECOND, 59)
                            set(Calendar.MILLISECOND, 999)
                        }
                        newFilterParams.dateTo = calendar.timeInMillis
                        if (newFilterParams.dateTo <= newFilterParams.dateFrom) {
                            calendar.apply {
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            newFilterParams.dateFrom = calendar.timeInMillis
                        }
                        newFilterParams.filterByDate = true
                        updateDates()
                    }
                }

                buttonDialogApply.setOnClickListener {
                    presenter.onFilterChanged(newFilterParams)
                    dialog.dismiss()
                }
            }
        }
    }
}
