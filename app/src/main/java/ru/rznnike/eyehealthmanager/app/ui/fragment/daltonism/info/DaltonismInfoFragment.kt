package ru.rznnike.eyehealthmanager.app.ui.fragment.daltonism.info

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.daltonism.info.DaltonismInfoPresenter
import ru.rznnike.eyehealthmanager.app.presentation.daltonism.info.DaltonismInfoView
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.databinding.FragmentDaltonismInfoBinding

class DaltonismInfoFragment : BaseFragment(R.layout.fragment_daltonism_info), DaltonismInfoView {
    @InjectPresenter
    lateinit var presenter: DaltonismInfoPresenter

    private val binding by viewBinding(FragmentDaltonismInfoBinding::bind)

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
        textViewToolbarHeader.setText(R.string.test_daltonism)
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
