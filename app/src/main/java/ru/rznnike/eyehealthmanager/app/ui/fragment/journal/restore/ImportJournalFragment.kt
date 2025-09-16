package ru.rznnike.eyehealthmanager.app.ui.fragment.journal.restore

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import dev.androidbroadcast.vbpd.viewBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.journal.restore.ImportJournalPresenter
import ru.rznnike.eyehealthmanager.app.presentation.journal.restore.ImportJournalView
import ru.rznnike.eyehealthmanager.app.ui.item.TestTypeIndicatorItem
import ru.rznnike.eyehealthmanager.app.ui.view.EmptyDividerDecoration
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToMargin
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.app.utils.extensions.createFastAdapter
import ru.rznnike.eyehealthmanager.app.utils.extensions.setVisible
import ru.rznnike.eyehealthmanager.databinding.FragmentImportJournalBinding
import ru.rznnike.eyehealthmanager.domain.model.test.TestType

class ImportJournalFragment : BaseFragment(R.layout.fragment_import_journal), ImportJournalView {
    @InjectPresenter
    lateinit var presenter: ImportJournalPresenter

    private val binding by viewBinding(FragmentImportJournalBinding::bind)

    private lateinit var adapterBackups: FastAdapter<IItem<*>>
    private lateinit var itemAdapterBackups: ItemAdapter<IItem<*>>

    private val folderPicker = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        uri?.let {
            presenter.onFolderSelected(uri = uri, context = requireContext())
        }
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
            buttonStartImport.addSystemWindowInsetToMargin(bottom = true)
        }
        initToolbar()
        initRecyclerView()
        initOnClickListeners()
    }

    private fun initToolbar() = binding.toolbar.apply {
        textViewToolbarHeader.setText(R.string.import_string)
        buttonToolbarLeft.setImageResource(R.drawable.ic_back)
        buttonToolbarLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initAdapter() {
        itemAdapterBackups = ItemAdapter()
        adapterBackups = createFastAdapter(itemAdapterBackups)
        adapterBackups.setHasStableIds(true)
    }

    private fun initRecyclerView() = binding.apply {
        recyclerViewBackupData.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@ImportJournalFragment.adapterBackups
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
        buttonSelectFolder.setOnClickListener {
            selectImportFolder()
        }
        buttonOpenFolder.setOnClickListener {
            presenter.openImportFolder()
        }
        buttonStartImport.setOnClickListener {
            presenter.importFiles(context = requireContext())
        }
    }

    override fun populateData(folderPath: String?, availableImportTypes: List<TestType>) {
        binding.apply {
            textViewBackupFolderPath.text = (folderPath ?: "").ifBlank { getString(R.string.folder_not_selected) }
            buttonOpenFolder.isEnabled = !folderPath.isNullOrBlank()

            itemAdapterBackups.setNewList(
                TestType.entries.map {
                    TestTypeIndicatorItem(
                        testType = it,
                        available = availableImportTypes.contains(it)
                    )
                }
            )

            listOf(
                textViewBackupDataHeader,
                recyclerViewBackupData
            ).forEach {
                it.setVisible(!folderPath.isNullOrBlank())
            }
        }
    }

    override fun selectImportFolder() = folderPicker.launch(null)
}
