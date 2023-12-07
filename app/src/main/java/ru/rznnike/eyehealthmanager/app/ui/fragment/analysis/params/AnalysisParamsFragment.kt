package ru.rznnike.eyehealthmanager.app.ui.fragment.analysis.params

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dialog.showDatePicker
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.analysis.params.AnalysisParamsPresenter
import ru.rznnike.eyehealthmanager.app.presentation.analysis.params.AnalysisParamsView
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.databinding.FragmentAnalysisParamsBinding
import ru.rznnike.eyehealthmanager.domain.model.AnalysisParams
import ru.rznnike.eyehealthmanager.domain.model.enums.AnalysisType
import ru.rznnike.eyehealthmanager.domain.utils.toDate

class AnalysisParamsFragment : BaseFragment(R.layout.fragment_analysis_params), AnalysisParamsView {
    @InjectPresenter
    lateinit var presenter: AnalysisParamsPresenter

    private val binding by viewBinding(FragmentAnalysisParamsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            layoutToolbarContainer.addSystemWindowInsetToPadding(top = true)
            layoutControls.addSystemWindowInsetToPadding(bottom = true)
        }
        initToolbar()
        initOnClickListeners()
    }

    private fun initToolbar() = binding.toolbar.apply {
        textViewToolbarHeader.setText(R.string.analysis)
        buttonToolbarLeft.setImageResource(R.drawable.ic_back)
        buttonToolbarLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initOnClickListeners() = binding.apply {
        radioButtonAcuityOnly.setOnClickListener {
            presenter.onAnalysisTypeValueChanged(AnalysisType.ACUITY_ONLY)
        }
        radioButtonConsolidatedReport.setOnClickListener {
            presenter.onAnalysisTypeValueChanged(AnalysisType.CONSOLIDATED_REPORT)
        }
        checkBoxApplyDynamicCorrections.setOnClickListener {
            presenter.onCheckBoxApplyDynamicCorrectionsClicked(checkBoxApplyDynamicCorrections.isChecked)
        }
        buttonStartAnalysis.setOnClickListener {
            presenter.onStartAnalysis()
        }
    }

    override fun populateData(params: AnalysisParams) {
        binding.apply {
            radioButtonAcuityOnly.isChecked = params.analysisType == AnalysisType.ACUITY_ONLY
            radioButtonConsolidatedReport.isChecked = params.analysisType == AnalysisType.CONSOLIDATED_REPORT
            checkBoxApplyDynamicCorrections.isChecked = params.applyDynamicCorrections

            buttonDateFrom.text = params.dateFrom.toDate()
            buttonDateTo.text = params.dateTo.toDate()

            buttonDateFrom.setOnClickListener {
                showDatePicker(
                    preselectedDate = params.dateFrom,
                    onSuccess = presenter::onDateFromValueChanged
                )
            }
            buttonDateTo.setOnClickListener {
                showDatePicker(
                    preselectedDate = params.dateFrom,
                    onSuccess = presenter::onDateToValueChanged
                )
            }
        }
    }
}
