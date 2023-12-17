package ru.rznnike.eyehealthmanager.app.ui.fragment.colorperception.test

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogAction
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogParameters
import ru.rznnike.eyehealthmanager.app.dialog.showAlertDialog
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.colorperception.test.ColorPerceptionTestPresenter
import ru.rznnike.eyehealthmanager.app.presentation.colorperception.test.ColorPerceptionTestView
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.app.utils.extensions.setVisible
import ru.rznnike.eyehealthmanager.app.utils.extensions.withEndActionSafe
import ru.rznnike.eyehealthmanager.databinding.FragmentColorPerceptionTestBinding

private const val FADE_ANIMATION_MS = 500L

class ColorPerceptionTestFragment : BaseFragment(R.layout.fragment_color_perception_test),
    ColorPerceptionTestView {
    @InjectPresenter
    lateinit var presenter: ColorPerceptionTestPresenter

    private val binding by viewBinding(FragmentColorPerceptionTestBinding::bind)

    override var progressCallback: ((Boolean) -> Unit)? = { show ->
        binding.apply {
            progressView.setProgress(show)
        }
    }

    override fun onBackPressed() {
        showExitDialog()
    }

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
        buttonYes.setOnClickListener {
            presenter.answer(true)
        }
        buttonNo.setOnClickListener {
            presenter.answer(false)
        }
    }

    override fun populateData(color1: Int, color2: Int, progress: Int) {
        binding.apply {
            percentProgressView.progress = progress

            buttonYes.isEnabled = false
            buttonNo.isEnabled = false

            layoutColors.animate()
                .alpha(0f)
                .setStartDelay(0)
                .setDuration(FADE_ANIMATION_MS)
                .withEndActionSafe(this@ColorPerceptionTestFragment) {
                    imageViewLeftColor.setBackgroundColor(color1)
                    imageViewRightColor.setBackgroundColor(color2)

                    layoutColors.setVisible()
                    layoutColors.animate()
                        .alpha(1f)
                        .setStartDelay(0)
                        .setDuration(FADE_ANIMATION_MS)
                        .withEndActionSafe(this@ColorPerceptionTestFragment) {
                            buttonYes.isEnabled = true
                            buttonNo.isEnabled = true
                        }
                        .start()
                }
                .start()
        }
    }

    private fun showExitDialog() {
        showAlertDialog(
            parameters = AlertDialogParameters.HORIZONTAL_2_OPTIONS_LEFT_ACCENT,
            header = getString(R.string.test_cancel_message),
            cancellable = true,
            actions = listOf(
                AlertDialogAction(getString(R.string.cancel)) {
                    it.dismiss()
                },
                AlertDialogAction(getString(R.string.exit)) {
                    it.dismiss()
                    routerFinishFlow()
                }
            )
        )
    }
}
