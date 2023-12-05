package ru.rznnike.eyehealthmanager.app.ui.fragment.nearfar.info

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.nearfar.info.NearFarInfoPresenter
import ru.rznnike.eyehealthmanager.app.presentation.nearfar.info.NearFarInfoView
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.databinding.FragmentNearFarInfoBinding

class NearFarInfoFragment : BaseFragment(R.layout.fragment_near_far_info), NearFarInfoView {
    @InjectPresenter
    lateinit var presenter: NearFarInfoPresenter

    private val binding by viewBinding(FragmentNearFarInfoBinding::bind)

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
        textViewToolbarHeader.setText(R.string.test_nearsightedness_farsightedness)
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
}
