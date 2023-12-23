package ru.rznnike.eyehealthmanager.app.ui.fragment.settings.testing

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogAction
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogParameters
import ru.rznnike.eyehealthmanager.app.dialog.showAlertDialog
import ru.rznnike.eyehealthmanager.app.dialog.showTimePicker
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.settings.testing.TestingSettingsPresenter
import ru.rznnike.eyehealthmanager.app.presentation.settings.testing.TestingSettingsView
import ru.rznnike.eyehealthmanager.app.utils.extensions.*
import ru.rznnike.eyehealthmanager.databinding.FragmentSettingsTestingBinding
import ru.rznnike.eyehealthmanager.domain.model.TestingSettings
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.toDate
import java.util.*

class TestingSettingsFragment : BaseFragment(R.layout.fragment_settings_testing), TestingSettingsView {
    @InjectPresenter
    lateinit var presenter: TestingSettingsPresenter

    private val binding by viewBinding(FragmentSettingsTestingBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            layoutToolbarContainer.addSystemWindowInsetToPadding(top = true)
            layoutScrollableContent.addSystemWindowInsetToPadding(bottom = true)
        }
        initToolbar()
        initOnClickListeners()
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    private fun initToolbar() = binding.toolbar.apply {
        textViewToolbarHeader.setText(R.string.settings_testing)
        buttonToolbarLeft.setImageResource(R.drawable.ic_back)
        buttonToolbarLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initOnClickListeners() = binding.apply {
        buttonEnterArmsLength.setOnClickListener {
            showArmsLengthInputDialog()
        }
        buttonEnterBodyHeight.setOnClickListener {
            showBodyHeightInputDialog()
        }
        buttonEnterLineLength.setOnClickListener {
            showScalingLineLengthInputDialog()
        }
        buttonResetScale.setOnClickListener {
            presenter.resetScale()
        }
        checkBoxReplaceBeginning.setOnClickListener {
            presenter.onCheckBoxReplaceBeginningClicked(checkBoxReplaceBeginning.isChecked)
        }
        checkBoxAutoDayPartSelection.setOnClickListener {
            presenter.onCheckBoxAutoDayPartSelectionClicked(checkBoxAutoDayPartSelection.isChecked)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun populateData(settings: TestingSettings) {
        binding.apply {
            textViewCurrentArmsLength.text = "<b>%d %s</b> - %s".format(
                settings.armsLength / 10,
                getString(R.string.centimeters_short),
                getString(R.string.current_arms_length)
            ).toHtmlSpanned()

            val finalDpmm = if (settings.dpmm > 0) settings.dpmm else requireContext().convertMmToPx(1f)
            layoutScalingLine.post {
                val widthMm = layoutScalingLine.width / finalDpmm
                textViewCurrentScale.text = "%s - <b>%.1f %s</b>".format(
                    getString(R.string.current_scale_message),
                    widthMm / 10,
                    getString(R.string.centimeters_short)
                ).toHtmlSpanned()
            }

            checkBoxReplaceBeginning.isChecked = settings.replaceBeginningWithMorning
            checkBoxAutoDayPartSelection.isChecked = settings.enableAutoDayPart

            textViewBeginning.setText(
                if (settings.replaceBeginningWithMorning) R.string.morning else R.string.beginning
            )
            textViewMiddle.setText(
                if (settings.replaceBeginningWithMorning) R.string.day else R.string.middle
            )
            textViewEnd.setText(
                if (settings.replaceBeginningWithMorning) R.string.evening else R.string.end
            )

            viewTimeControlsDisabler.setVisible(!settings.enableAutoDayPart)

            val calendar = Calendar.getInstance()
            val timeToDayBeginningWithOffset = settings.timeToDayBeginning - calendar.timeZone.rawOffset
            val timeToDayBeginningText = timeToDayBeginningWithOffset.toDate(GlobalConstants.DATE_PATTERN_CLOCK)

            val timeToDayMiddleWithOffset = settings.timeToDayMiddle - calendar.timeZone.rawOffset
            val timeToDayMiddleText = timeToDayMiddleWithOffset.toDate(GlobalConstants.DATE_PATTERN_CLOCK)

            val timeToDayEndWithOffset = settings.timeToDayEnd - calendar.timeZone.rawOffset
            val timeToDayEndText = timeToDayEndWithOffset.toDate(GlobalConstants.DATE_PATTERN_CLOCK)

            buttonTimeToBeginning1.text = timeToDayBeginningText
            buttonTimeToBeginning2.text = timeToDayBeginningText
            buttonTimeToMiddle1.text = timeToDayMiddleText
            buttonTimeToMiddle2.text = timeToDayMiddleText
            buttonTimeToEnd1.text = timeToDayEndText
            buttonTimeToEnd2.text = timeToDayEndText

            listOf(buttonTimeToBeginning1, buttonTimeToBeginning2).forEach {
                it.setOnClickListener {
                    showTimePicker(
                        preselectedTime = timeToDayBeginningWithOffset,
                        onSuccess = presenter::onTimeToDayBeginningValueChanged
                    )
                }
            }
            listOf(buttonTimeToMiddle1, buttonTimeToMiddle2).forEach {
                it.setOnClickListener {
                    showTimePicker(
                        preselectedTime = timeToDayMiddleWithOffset,
                        onSuccess = presenter::onTimeToDayMiddleValueChanged
                    )
                }
            }
            listOf(buttonTimeToEnd1, buttonTimeToEnd2).forEach {
                it.setOnClickListener {
                    showTimePicker(
                        preselectedTime = timeToDayEndWithOffset,
                        onSuccess = presenter::onTimeToDayEndValueChanged
                    )
                }
            }
        }
    }

    private fun showArmsLengthInputDialog() {
        var input = ""
        showAlertDialog(
            parameters = AlertDialogParameters.INPUT_INT,
            header = getString(R.string.input_arms_length),
            actions = listOf(
                AlertDialogAction(getString(R.string.save)) {
                    it.dismiss()
                    presenter.onArmsLengthValueChanged(input)
                }
            ),
            onInputListener = { input = it }
        )
    }

    private fun showBodyHeightInputDialog() {
        var input = ""
        showAlertDialog(
            parameters = AlertDialogParameters.INPUT_INT,
            header = getString(R.string.input_body_height),
            actions = listOf(
                AlertDialogAction(getString(R.string.save)) {
                    it.dismiss()
                    presenter.onBodyHeightValueChanged(input)
                }
            ),
            onInputListener = { input = it }
        )
    }

    private fun showScalingLineLengthInputDialog() {
        var input = ""
        showAlertDialog(
            parameters = AlertDialogParameters.INPUT_FLOAT,
            header = getString(R.string.input_line_length),
            actions = listOf(
                AlertDialogAction(getString(R.string.save)) {
                    it.dismiss()
                    presenter.onScalingLineLengthValueChanged(input, binding.layoutScalingLine.width)
                }
            ),
            onInputListener = { input = it }
        )
    }
}
