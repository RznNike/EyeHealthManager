package ru.rznnike.eyehealthmanager.app.ui.fragment.acuity.doctor

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dialog.showDatePicker
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.acuity.doctor.AcuityDoctorResultPresenter
import ru.rznnike.eyehealthmanager.app.presentation.acuity.doctor.AcuityDoctorResultView
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.app.utils.extensions.syncWithValue
import ru.rznnike.eyehealthmanager.databinding.FragmentAcuityDoctorResultBinding
import ru.rznnike.eyehealthmanager.domain.utils.GlobalConstants
import ru.rznnike.eyehealthmanager.domain.utils.toDate

class AcuityDoctorResultFragment : BaseFragment(R.layout.fragment_acuity_doctor_result),
    AcuityDoctorResultView {
    @InjectPresenter
    lateinit var presenter: AcuityDoctorResultPresenter

    private val binding by viewBinding(FragmentAcuityDoctorResultBinding::bind)

    override var progressCallback: ((Boolean) -> Unit)? = { show ->
        binding.apply {
            progressView.setProgress(show)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            layoutToolbarContainer.addSystemWindowInsetToPadding(top = true)
            layoutControls.addSystemWindowInsetToPadding(bottom = true)
        }
        initToolbar()
        initEditTextListeners()
        initOnClickListeners()
    }

    private fun initToolbar() = binding.toolbar.apply {
        textViewToolbarHeader.setText(R.string.doctor_test_results)
        buttonToolbarLeft.setImageResource(R.drawable.ic_back)
        buttonToolbarLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initEditTextListeners() = binding.apply {
        editTextLeftEye.addTextChangedListener {
            presenter.onLeftEyeValueChanged(it.toString())
        }
        editTextRightEye.addTextChangedListener {
            presenter.onRightEyeValueChanged(it.toString())
        }
    }

    private fun initOnClickListeners() = binding.apply {
        buttonAddDoctorResult.setOnClickListener {
            presenter.onAddResult()
        }
    }

    override fun populateData(date: Long?, leftEye: String, rightEye: String) {
        binding.apply {
            date?.let {
                buttonDate.text = date.toDate(GlobalConstants.DATE_PATTERN_SIMPLE_WITH_TIME)
            } ?: run {
                buttonDate.setText(R.string.choose_date_and_time)
            }
            buttonDate.setOnClickListener {
                showDatePicker(
                    preselectedDate = date,
                    enableTimePicker = true,
                    onSuccess = presenter::onDateTimeSelected
                )
            }
            editTextLeftEye.syncWithValue(leftEye)
            editTextRightEye.syncWithValue(rightEye)
        }
    }
}
