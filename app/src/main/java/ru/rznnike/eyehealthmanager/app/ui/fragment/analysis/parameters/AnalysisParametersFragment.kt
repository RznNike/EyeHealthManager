package ru.rznnike.eyehealthmanager.app.ui.fragment.analysis.parameters

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogAction
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogParameters
import ru.rznnike.eyehealthmanager.app.dialog.showAlertDialog
import ru.rznnike.eyehealthmanager.app.dialog.showDatePicker
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.analysis.parameters.AnalysisParametersPresenter
import ru.rznnike.eyehealthmanager.app.presentation.analysis.parameters.AnalysisParametersView
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToMargin
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.databinding.FragmentAnalysisParametersBinding
import ru.rznnike.eyehealthmanager.domain.model.AnalysisParameters
import ru.rznnike.eyehealthmanager.domain.model.enums.AnalysisType
import ru.rznnike.eyehealthmanager.domain.utils.toDate

class AnalysisParametersFragment : BaseFragment(R.layout.fragment_analysis_parameters), AnalysisParametersView {
    @InjectPresenter
    lateinit var presenter: AnalysisParametersPresenter

    private val binding by viewBinding(FragmentAnalysisParametersBinding::bind)

    override var progressCallback: ((Boolean) -> Unit)? = { show ->
        binding.apply {
            progressView.setProgress(show)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            layoutToolbarContainer.addSystemWindowInsetToPadding(top = true)
            layoutScrollableContent.addSystemWindowInsetToPadding(bottom = true)
            buttonStartAnalysis.addSystemWindowInsetToMargin(bottom = true)
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
            presenter.onApplyDynamicCorrectionsChanged(checkBoxApplyDynamicCorrections.isChecked)
        }
        buttonStartAnalysis.setOnClickListener {
            presenter.startAnalysis()
        }
    }

    override fun populateData(parameters: AnalysisParameters) {
        binding.apply {
            radioButtonAcuityOnly.isChecked = parameters.analysisType == AnalysisType.ACUITY_ONLY
            radioButtonConsolidatedReport.isChecked = parameters.analysisType == AnalysisType.CONSOLIDATED_REPORT
            checkBoxApplyDynamicCorrections.isChecked = parameters.applyDynamicCorrections

            buttonDateFrom.text = parameters.dateFrom.toDate()
            buttonDateTo.text = parameters.dateTo.toDate()

            buttonDateFrom.setOnClickListener {
                showDatePicker(
                    preselectedDate = parameters.dateFrom,
                    onSuccess = presenter::onDateFromValueChanged
                )
            }
            buttonDateTo.setOnClickListener {
                showDatePicker(
                    preselectedDate = parameters.dateFrom,
                    onSuccess = presenter::onDateToValueChanged
                )
            }
        }
    }

    override fun showNotEnoughDataMessage() {
        showAlertDialog(
            parameters = AlertDialogParameters.VERTICAL_1_OPTION_ACCENT,
            header = getString(R.string.not_enough_data_analysis_header),
            message = getString(R.string.not_enough_data_analysis_message),
            actions = listOf(
                AlertDialogAction(getString(R.string.ok)) {
                    it.dismiss()
                }
            )
        )
    }
}
