package ru.rznnike.eyehealthmanager.app.ui.fragment.journal.backup

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dialog.showDatePicker
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.journal.backup.ExportJournalPresenter
import ru.rznnike.eyehealthmanager.app.presentation.journal.backup.ExportJournalView
import ru.rznnike.eyehealthmanager.app.ui.item.TestTypeSmallItem
import ru.rznnike.eyehealthmanager.app.ui.view.EmptyDividerDecoration
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToMargin
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.app.utils.extensions.createFastAdapter
import ru.rznnike.eyehealthmanager.databinding.FragmentExportJournalBinding
import ru.rznnike.eyehealthmanager.domain.model.journal.TestResultFilter
import ru.rznnike.eyehealthmanager.domain.model.test.TestType
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.toDate

class ExportJournalFragment : BaseFragment(R.layout.fragment_export_journal), ExportJournalView {
    @InjectPresenter
    lateinit var presenter: ExportJournalPresenter

    private val binding by viewBinding(FragmentExportJournalBinding::bind)

    private lateinit var adapterTestType: FastAdapter<IItem<*>>
    private lateinit var itemAdapterTestType: ItemAdapter<IItem<*>>

    private val folderPicker = object {
        private val launcher = registerForActivityResult(
            object : ActivityResultContracts.OpenDocumentTree() {
                override fun createIntent(context: Context, input: Uri?) =
                    super.createIntent(context, input).apply {
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                    }
            }
        ) { uri ->
            uri?.let {
                presenter.onFolderSelected(uri)
            }
        }

        fun launch() = launcher.launch(null)
    }

    override var progressCallback: ((Boolean) -> Unit)? = { show ->
        binding.apply {
            progressView.setProgress(show)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            layoutToolbarContainer.addSystemWindowInsetToPadding(top = true)
            layoutScrollableContent.addSystemWindowInsetToPadding(bottom = true)
            buttonStartExport.addSystemWindowInsetToMargin(bottom = true)
        }
        initToolbar()
        initRecyclerView()
        initOnClickListeners()
    }

    private fun initToolbar() = binding.toolbar.apply {
        textViewToolbarHeader.setText(R.string.export)
        buttonToolbarLeft.setImageResource(R.drawable.ic_back)
        buttonToolbarLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initAdapter() {
        itemAdapterTestType = ItemAdapter()
        adapterTestType = createFastAdapter(itemAdapterTestType)
        adapterTestType.setHasStableIds(true)

        adapterTestType.onClickListener = { _, _, item, _ ->
            when (item) {
                is TestTypeSmallItem -> {
                    presenter.onFilterTestTypeClick(item.testType)
                    true
                }
                else -> false
            }
        }
    }

    private fun initRecyclerView() = binding.apply {
        recyclerViewFilterTypes.apply {
            layoutManager = FlexboxLayoutManager(requireContext(), FlexDirection.ROW)
            adapter = this@ExportJournalFragment.adapterTestType
            itemAnimator = null
            addItemDecoration(
                EmptyDividerDecoration(
                    context = requireContext(),
                    cardInsets = R.dimen.baseline_grid_4,
                    applyOutsideDecoration = false
                )
            )
        }
    }

    private fun initOnClickListeners() = binding.apply {
        buttonClearFilters.setOnClickListener {
            presenter.clearFilters()
        }
        checkBoxFilterByDate.setOnClickListener {
            presenter.onFilterByDateValueChanged(checkBoxFilterByDate.isChecked)
        }
        checkBoxFilterByType.setOnClickListener {
            presenter.onFilterByTypeValueChanged(checkBoxFilterByType.isChecked)
        }
        buttonSelectFolder.setOnClickListener {
            selectExportFolder()
        }
        buttonOpenFolder.setOnClickListener {
            presenter.openExportFolder()
        }
        buttonStartExport.setOnClickListener {
            presenter.startExport()
        }
    }

    override fun populateData(filter: TestResultFilter, folderPath: String?) {
        binding.apply {
            checkBoxFilterByDate.isChecked = filter.filterByDate
            buttonDateFrom.text = filter.dateFrom.toDate(GlobalConstants.DATE_PATTERN_SIMPLE)
            buttonDateTo.text = filter.dateTo.toDate(GlobalConstants.DATE_PATTERN_SIMPLE)
            buttonDateFrom.setOnClickListener {
                showDatePicker(
                    preselectedDate = filter.dateFrom,
                    onSuccess = presenter::onFilterDateFromSelected
                )
            }
            buttonDateTo.setOnClickListener {
                showDatePicker(
                    preselectedDate = filter.dateTo,
                    onSuccess = presenter::onFilterDateToSelected
                )
            }

            checkBoxFilterByType.isChecked = filter.filterByType
            itemAdapterTestType.setNewList(
                TestType.entries.map {
                    TestTypeSmallItem(it).also { item ->
                        item.isSelected = filter.selectedTestTypes.contains(it)
                    }
                }
            )

            textViewBackupFolderPath.text = (folderPath ?: "").ifBlank { getString(R.string.folder_not_selected) }
            buttonOpenFolder.isEnabled = !folderPath.isNullOrBlank()
        }
    }

    override fun selectExportFolder() = folderPicker.launch()
}
