package ru.rznnike.eyehealthmanager.app.ui.fragment.main.journal

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.mikepenz.fastadapter.FastAdapter
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
import ru.rznnike.eyehealthmanager.domain.model.test.TestResult
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultFilter
import ru.rznnike.eyehealthmanager.domain.model.test.TestType
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.atEndOfDay
import ru.rznnike.eyehealthmanager.domain.utils.millis
import ru.rznnike.eyehealthmanager.domain.utils.toDate
import ru.rznnike.eyehealthmanager.domain.utils.toLocalDate

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
        initOnClickListeners()
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
                    cardInsets = R.dimen.baseline_grid_8,
                    applyOutsideDecoration = false
                )
            )
        }
    }

    private fun initOnClickListeners() = binding.apply {
        buttonTools.setOnClickListener {
            showToolsDialog()
        }
    }

    override fun populateData(data: List<TestResult>, filter: TestResultFilter) {
        binding.apply {
            itemAdapter.setNewList(data.map { TestResultItem(it) })
            zeroView.setVisible(data.isEmpty())
            buttonFilter.setOnClickListener {
                showFilterDialog(filter)
            }
            imageViewFilterIcon.setVisible(
                filter.filterByDate || filter.filterByType
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

    private fun showToolsDialog() {
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
                }
            )
        )
    }

    private fun showFilterDialog(filter: TestResultFilter) {
        BottomDialogJournalFiltersBinding.inflate(layoutInflater).apply {
            showCustomBottomDialog(
                rootView = root,
                constraintView = rootDialogLayout,
                constraintChildView = layoutDialogContent
            ) { dialog ->
                layoutDialogContent.addSystemWindowInsetToPadding(bottom = true)

                val newFilter = filter.deepCopy()

                val itemAdapterTestType: ItemAdapter<IItem<*>> = ItemAdapter()
                val adapterTestType: FastAdapter<IItem<*>> = createFastAdapter(itemAdapterTestType)
                adapterTestType.setHasStableIds(true)

                fun updateTestTypes() {
                    itemAdapterTestType.setNewList(
                        TestType.entries.map {
                            TestTypeSmallItem(it).also { item ->
                                item.isSelected = newFilter.selectedTestTypes.contains(it)
                            }
                        }
                    )
                    checkBoxFilterByType.isChecked = newFilter.filterByType
                }

                fun onFilterTestTypeClick(testType: TestType) {
                    if (newFilter.selectedTestTypes.contains(testType)) {
                        newFilter.selectedTestTypes.remove(testType)
                    } else {
                        newFilter.selectedTestTypes.add(testType)
                    }
                    newFilter.filterByType = newFilter.selectedTestTypes.isNotEmpty()
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
                    layoutManager = FlexboxLayoutManager(requireContext(), FlexDirection.ROW)
                    adapter = adapterTestType
                    itemAnimator = null
                    addItemDecoration(
                        EmptyDividerDecoration(
                            context = requireContext(),
                            cardInsets = R.dimen.baseline_grid_4,
                            applyOutsideDecoration = false
                        )
                    )
                }

                updateTestTypes()

                checkBoxFilterByDate.setOnClickListener {
                    newFilter.filterByDate = checkBoxFilterByDate.isChecked
                }
                checkBoxFilterByType.setOnClickListener {
                    newFilter.filterByType = checkBoxFilterByType.isChecked
                }

                fun updateDates() {
                    checkBoxFilterByDate.isChecked = newFilter.filterByDate
                    buttonDateFrom.text = newFilter.dateFrom.toDate()
                    buttonDateTo.text = newFilter.dateTo.toDate()
                }
                updateDates()

                buttonDateFrom.setOnClickListener {
                    showDatePicker(
                        preselectedDate = newFilter.dateFrom
                    ) { timestamp ->
                        newFilter.dateFrom = timestamp.toLocalDate().atStartOfDay().millis()
                        if (newFilter.dateTo <= newFilter.dateFrom) {
                            newFilter.dateTo = timestamp.toLocalDate().atEndOfDay().millis()
                        }
                        newFilter.filterByDate = true
                        updateDates()
                    }
                }
                buttonDateTo.setOnClickListener {
                    showDatePicker(
                        preselectedDate = newFilter.dateTo
                    ) { timestamp ->
                        newFilter.dateTo = timestamp.toLocalDate().atEndOfDay().millis()
                        if (newFilter.dateTo <= newFilter.dateFrom) {
                            newFilter.dateFrom = timestamp.toLocalDate().atStartOfDay().millis()
                        }
                        newFilter.filterByDate = true
                        updateDates()
                    }
                }

                buttonClearFilters.setOnClickListener {
                    presenter.clearFilter()
                    dialog.dismiss()
                }
                buttonApplyFilters.setOnClickListener {
                    presenter.onFilterChanged(newFilter)
                    dialog.dismiss()
                }
            }
        }
    }
}
