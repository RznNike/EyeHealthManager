package ru.rznnike.eyehealthmanager.app.ui.fragment.colorperception.info

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.colorperception.info.ColorPerceptionInfoPresenter
import ru.rznnike.eyehealthmanager.app.presentation.colorperception.info.ColorPerceptionInfoView
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.databinding.FragmentColorPerceptionInfoBinding

class ColorPerceptionInfoFragment : BaseFragment(R.layout.fragment_color_perception_info),
    ColorPerceptionInfoView {
    @InjectPresenter
    lateinit var presenter: ColorPerceptionInfoPresenter

    private val binding by viewBinding(FragmentColorPerceptionInfoBinding::bind)

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
        textViewToolbarHeader.setText(R.string.test_color_perception)
        buttonToolbarLeft.setImageResource(R.drawable.ic_back)
        buttonToolbarLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initOnClickListeners() = binding.apply {
        buttonStartTest.setOnClickListener {
            presenter.onStart()
        }
        buttonDaltonismTest.setOnClickListener {
            presenter.onDaltonismTest()
        }
    }
}
