package ru.rznnike.eyehealthmanager.app.ui.fragment.contrast.info

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.contrast.info.ContrastInfoPresenter
import ru.rznnike.eyehealthmanager.app.presentation.contrast.info.ContrastInfoView
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.databinding.FragmentContrastInfoBinding

class ContrastInfoFragment : BaseFragment(R.layout.fragment_contrast_info), ContrastInfoView {
    @InjectPresenter
    lateinit var presenter: ContrastInfoPresenter

    private val binding by viewBinding(FragmentContrastInfoBinding::bind)

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
        textViewToolbarHeader.setText(R.string.test_contrast)
        buttonToolbarLeft.setImageResource(R.drawable.ic_back)
        buttonToolbarLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initOnClickListeners() = binding.apply {
        buttonStartTest.setOnClickListener {
            presenter.startTest()
        }
    }
}
