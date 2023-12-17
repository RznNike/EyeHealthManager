package ru.rznnike.eyehealthmanager.app.ui.fragment.acuity.info

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import moxy.presenter.InjectPresenter
import net.cachapa.expandablelayout.ExpandableLayout
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.acuity.info.AcuityInfoPresenter
import ru.rznnike.eyehealthmanager.app.presentation.acuity.info.AcuityInfoView
import ru.rznnike.eyehealthmanager.app.ui.item.EyesTypeItem
import ru.rznnike.eyehealthmanager.app.ui.item.SymbolsTypeItem
import ru.rznnike.eyehealthmanager.app.ui.view.EmptyDividerDecoration
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.app.utils.extensions.createFastAdapter
import ru.rznnike.eyehealthmanager.databinding.DialogDayPartSelectionBinding
import ru.rznnike.eyehealthmanager.databinding.FragmentAcuityInfoBinding
import ru.rznnike.eyehealthmanager.domain.model.AcuityTestingSettings
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.enums.DayPart
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType

class AcuityInfoFragment : BaseFragment(R.layout.fragment_acuity_info), AcuityInfoView {
    @InjectPresenter
    lateinit var presenter: AcuityInfoPresenter

    private val binding by viewBinding(FragmentAcuityInfoBinding::bind)

    private lateinit var adapterSymbolsType: FastAdapter<IItem<*>>
    private lateinit var itemAdapterSymbolsType: ItemAdapter<IItem<*>>

    private lateinit var adapterEyesType: FastAdapter<IItem<*>>
    private lateinit var itemAdapterEyesType: ItemAdapter<IItem<*>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapters()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            layoutToolbarContainer.addSystemWindowInsetToPadding(top = true)
            layoutControls.addSystemWindowInsetToPadding(bottom = true)
        }
        initToolbar()
        initViews()
        initRecyclerViews()
        initOnClickListeners()
    }

    private fun initToolbar() = binding.toolbar.apply {
        textViewToolbarHeader.setText(R.string.test_acuity)
        buttonToolbarLeft.setImageResource(R.drawable.ic_back)
        buttonToolbarLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initAdapters() {
        itemAdapterSymbolsType = ItemAdapter()
        adapterSymbolsType = createFastAdapter(itemAdapterSymbolsType)
        adapterSymbolsType.setHasStableIds(true)

        adapterSymbolsType.onClickListener = { _, _, item, _ ->
            when (item) {
                is SymbolsTypeItem -> {
                    presenter.onSymbolsTypeSelected(item.symbolsType)
                    true
                }
                else -> false
            }
        }

        itemAdapterEyesType = ItemAdapter()
        adapterEyesType = createFastAdapter(itemAdapterEyesType)
        adapterEyesType.setHasStableIds(true)

        adapterEyesType.onClickListener = { _, _, item, _ ->
            when (item) {
                is EyesTypeItem -> {
                    presenter.onEyesTypeSelected(item.eyesType)
                    true
                }
                else -> false
            }
        }
    }

    private fun initRecyclerViews() {
        binding.apply {
            recyclerViewSymbolsType.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = this@AcuityInfoFragment.adapterSymbolsType
                itemAnimator = null
                addItemDecoration(
                    EmptyDividerDecoration(
                        context = requireContext(),
                        cardInsets = R.dimen.baseline_grid_8,
                        applyOutsideDecoration = false
                    )
                )
            }

            recyclerViewEyesType.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = this@AcuityInfoFragment.adapterEyesType
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

    private fun initViews() = binding.apply {
        expandableLayoutSettings.setOnExpansionUpdateListener { _, state ->
            listOf(imageViewSettingsArrow1, imageViewSettingsArrow2).forEach {
                it.setImageResource(
                    if (state == ExpandableLayout.State.EXPANDED) R.drawable.ic_arrow_button_down else R.drawable.ic_arrow_button_up
                )
            }
        }
    }

    private fun initOnClickListeners() = binding.apply {
        buttonStartTest.setOnClickListener {
            presenter.onPrepareToStartTest()
        }
        buttonTestSettings.setOnClickListener {
            expandableLayoutSettings.toggle()
        }
        buttonScaleSettings.setOnClickListener {
            presenter.onScaleSettings()
        }
        buttonAddDoctorResult.setOnClickListener {
            presenter.onAddDoctorResult()
        }
    }

    override fun populateData(acuitySettings: AcuityTestingSettings) {
        itemAdapterSymbolsType.setNewList(
            AcuityTestSymbolsType.entries.map {
                SymbolsTypeItem(it).also { item ->
                    item.isSelected = item.symbolsType == acuitySettings.symbolsType
                }
            }
        )
        itemAdapterEyesType.setNewList(
            TestEyesType.entries.map {
                EyesTypeItem(it).also { item ->
                    item.isSelected = item.eyesType == acuitySettings.eyesType
                }
            }
        )
    }

    override fun showDayPartSelectionDialog(replaceBeginningWithMorning: Boolean) {
        DialogDayPartSelectionBinding.inflate(layoutInflater).apply {
            val dialog = AlertDialog.Builder(requireContext(), R.style.AppTheme_Dialog_Alert)
                .setView(root)
                .setCancelable(true)
                .create()

            buttonDialogBeginning.setText(
                if (replaceBeginningWithMorning) R.string.morning else R.string.beginning
            )
            buttonDialogBeginning.setOnClickListener {
                dialog.dismiss()
                presenter.startTest(DayPart.BEGINNING)
            }

            buttonDialogMiddle.setText(
                if (replaceBeginningWithMorning) R.string.day else R.string.middle
            )
            buttonDialogMiddle.setOnClickListener {
                dialog.dismiss()
                presenter.startTest(DayPart.MIDDLE)
            }

            buttonDialogEnd.setText(
                if (replaceBeginningWithMorning) R.string.evening else R.string.end
            )
            buttonDialogEnd.setOnClickListener {
                dialog.dismiss()
                presenter.startTest(DayPart.END)
            }

            buttonDialogSchedule.setOnClickListener {
                dialog.dismiss()
                presenter.onDayPartAutoSelectionSettings()
            }

            dialog.show()
        }
    }
}
