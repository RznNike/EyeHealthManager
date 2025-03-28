package ru.rznnike.eyehealthmanager.app.ui.fragment.main.tests

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dev.androidbroadcast.vbpd.viewBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.main.tests.TestsPresenter
import ru.rznnike.eyehealthmanager.app.presentation.main.tests.TestsView
import ru.rznnike.eyehealthmanager.app.ui.item.TestTypeItem
import ru.rznnike.eyehealthmanager.app.ui.view.EmptyDividerDecoration
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.app.utils.extensions.createFastAdapter
import ru.rznnike.eyehealthmanager.databinding.FragmentTestsBinding
import ru.rznnike.eyehealthmanager.domain.model.test.TestType

class TestsFragment : BaseFragment(R.layout.fragment_tests), TestsView {
    @InjectPresenter
    lateinit var presenter: TestsPresenter

    private val binding by viewBinding(FragmentTestsBinding::bind)

    private lateinit var adapter: FastAdapter<IItem<*>>
    private lateinit var itemAdapter: ItemAdapter<IItem<*>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapters()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            recyclerView.addSystemWindowInsetToPadding(top = true)
        }
        initRecyclerViews()
        populateData()
    }

    private fun populateData() {
        itemAdapter.setNewList(
            TestType.entries.map { TestTypeItem(it) }
        )
    }

    private fun initAdapters() {
        itemAdapter = ItemAdapter()
        adapter = createFastAdapter(itemAdapter)
        adapter.setHasStableIds(true)

        adapter.onClickListener = { _, _, item, _ ->
            when (item) {
                is TestTypeItem -> {
                    presenter.onSelectTest(item.testType)
                    true
                }
                else -> false
            }
        }
    }

    private fun initRecyclerViews() = binding.apply {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@TestsFragment.adapter
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
}
