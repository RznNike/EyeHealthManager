package ru.rznnike.eyehealthmanager.app.ui.fragment.acuity.instruction

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.acuity.instruction.AcuityInstructionPresenter
import ru.rznnike.eyehealthmanager.app.presentation.acuity.instruction.AcuityInstructionView
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.app.utils.extensions.getIntArg
import ru.rznnike.eyehealthmanager.databinding.FragmentAcuityInstructionBinding
import ru.rznnike.eyehealthmanager.domain.model.enums.DayPart

class AcuityInstructionFragment : BaseFragment(R.layout.fragment_acuity_instruction),
    AcuityInstructionView {
    @InjectPresenter
    lateinit var presenter: AcuityInstructionPresenter

    @ProvidePresenter
    fun providePresenter() = AcuityInstructionPresenter(
        dayPart = DayPart[getIntArg(DAY_PART)]
    )

    private val binding by viewBinding(FragmentAcuityInstructionBinding::bind)

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
        textViewToolbarHeader.setText(R.string.instruction)
        buttonToolbarLeft.setImageResource(R.drawable.ic_back)
        buttonToolbarLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initOnClickListeners() = binding.apply {
        buttonStartTest.setOnClickListener {
            presenter.onStart()
        }
    }

    companion object {
        const val DAY_PART = "DAY_PART"
    }
}
