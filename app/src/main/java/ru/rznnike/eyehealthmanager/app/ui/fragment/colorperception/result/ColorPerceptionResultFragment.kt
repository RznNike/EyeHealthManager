package ru.rznnike.eyehealthmanager.app.ui.fragment.colorperception.result

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import dev.androidbroadcast.vbpd.viewBinding
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.colorperception.result.ColorPerceptionResultPresenter
import ru.rznnike.eyehealthmanager.app.presentation.colorperception.result.ColorPerceptionResultView
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToMargin
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.app.utils.extensions.getIntArg
import ru.rznnike.eyehealthmanager.databinding.FragmentColorPerceptionResultBinding

class ColorPerceptionResultFragment : BaseFragment(R.layout.fragment_color_perception_result),
    ColorPerceptionResultView {
    @InjectPresenter
    lateinit var presenter: ColorPerceptionResultPresenter

    @ProvidePresenter
    fun providePresenter() = ColorPerceptionResultPresenter(
        recognizedCount = getIntArg(RECOGNIZED_COUNT),
        allCount = getIntArg(ALL_COUNT)
    )

    private val binding by viewBinding(FragmentColorPerceptionResultBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            layoutToolbarContainer.addSystemWindowInsetToPadding(top = true)
            layoutScrollableContent.addSystemWindowInsetToPadding(bottom = true)
            buttonClose.addSystemWindowInsetToMargin(bottom = true)
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
        buttonClose.setOnClickListener {
            routerFinishFlow()
        }
    }

    override fun populateData(recognizedCount: Int, allCount: Int, @StringRes messageResId: Int) {
        binding.apply {
            textViewResult.text = "%s/%s".format(
                recognizedCount,
                allCount
            )
            textViewMessage.setText(messageResId)
        }
    }

    companion object {
        const val RECOGNIZED_COUNT = "RECOGNIZED_COUNT"
        const val ALL_COUNT = "ALL_COUNT"
    }
}
