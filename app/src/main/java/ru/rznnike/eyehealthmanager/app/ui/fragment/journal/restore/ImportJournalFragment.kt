package ru.rznnike.eyehealthmanager.app.ui.fragment.journal.restore

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
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
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.app.utils.extensions.createFastAdapter
import ru.rznnike.eyehealthmanager.app.utils.extensions.setVisible
import ru.rznnike.eyehealthmanager.databinding.FragmentImportJournalBinding
import ru.rznnike.eyehealthmanager.domain.model.enums.TestType

class ImportJournalFragment : BaseFragment(R.layout.fragment_import_journal), ImportJournalView {
    @InjectPresenter
    lateinit var presenter: ImportJournalPresenter

    private val binding by viewBinding(FragmentImportJournalBinding::bind)

    private lateinit var adapterBackups: FastAdapter<IItem<*>>
    private lateinit var itemAdapterBackups: ItemAdapter<IItem<*>>

    private val folderPicker = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        uri?.let {
            presenter.onFolderSelected(uri)
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
            layoutControls.addSystemWindowInsetToPadding(bottom = true)
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
        recyclerView.apply {
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
        buttonSelectImportFolder.setOnClickListener {
            selectImportFolder()
        }
        buttonOpenImportFolder.setOnClickListener {
            presenter.openImportFolder()
        }
        buttonStartImport.setOnClickListener {
            presenter.importFiles()
        }
    }

    override fun populateData(folderPath: String?, availableImportTypes: List<TestType>) {
        binding.apply {
            itemAdapterBackups.setNewList(
                TestType.entries.map {
                    TestTypeIndicatorItem(
                        testType = it,
                        available = availableImportTypes.contains(it)
                    )
                }
            )
            textViewBackupFolderPath.text = folderPath
            textViewBackupFolderPath.setVisible(!folderPath.isNullOrBlank())
            buttonOpenImportFolder.setVisible(!folderPath.isNullOrBlank())
        }
    }

    override fun selectImportFolder() = folderPicker.launch(null)
}
