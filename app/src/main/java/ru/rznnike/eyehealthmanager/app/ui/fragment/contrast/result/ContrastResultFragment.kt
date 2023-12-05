package ru.rznnike.eyehealthmanager.app.ui.fragment.contrast.result

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.contrast.result.ContrastResultPresenter
import ru.rznnike.eyehealthmanager.app.presentation.contrast.result.ContrastResultView
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.app.utils.extensions.getIntArg
import ru.rznnike.eyehealthmanager.databinding.FragmentContrastResultBinding

class ContrastResultFragment : BaseFragment(R.layout.fragment_contrast_result), ContrastResultView {
    @InjectPresenter
    lateinit var presenter: ContrastResultPresenter

    @ProvidePresenter
    fun providePresenter() = ContrastResultPresenter(
        recognizedDelta = getIntArg(RECOGNIZED_DELTA)
    )

    private val binding by viewBinding(FragmentContrastResultBinding::bind)

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
        buttonClose.setOnClickListener {
            routerFinishFlow()
        }
    }

    override fun populateData(recognizedDelta: Int, @StringRes messageResId: Int) {
        binding.apply {
            textViewResult.text = "%s%%".format(
                recognizedDelta
            )
            textViewMessage.setText(messageResId)
        }
    }

    companion object {
        const val RECOGNIZED_DELTA = "RECOGNIZED_DELTA"
    }
}
