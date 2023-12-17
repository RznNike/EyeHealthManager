package ru.rznnike.eyehealthmanager.app.ui.fragment.contrast.test

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogAction
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogParameters
import ru.rznnike.eyehealthmanager.app.dialog.showAlertDialog
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.contrast.test.ContrastTestPresenter
import ru.rznnike.eyehealthmanager.app.presentation.contrast.test.ContrastTestView
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.app.utils.extensions.setVisible
import ru.rznnike.eyehealthmanager.app.utils.extensions.withEndActionSafe
import ru.rznnike.eyehealthmanager.databinding.FragmentContrastTestBinding
import ru.rznnike.eyehealthmanager.domain.model.enums.Direction

private const val FADE_ANIMATION_MS = 500L

class ContrastTestFragment : BaseFragment(R.layout.fragment_contrast_test), ContrastTestView {
    @InjectPresenter
    lateinit var presenter: ContrastTestPresenter

    private val binding by viewBinding(FragmentContrastTestBinding::bind)

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
            content.addSystemWindowInsetToPadding(bottom = true)
        }
        initToolbar()
        initOnClickListeners()
    }

    override fun onStart() {
        super.onStart()
        activity?.window?.apply {
            attributes = attributes.apply {
                screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
            }
        }
    }

    override fun onStop() {
        activity?.window?.apply {
            attributes = attributes.apply {
                screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            }
        }
        super.onStop()
    }

    private fun initToolbar() = binding.toolbar.apply {
        textViewToolbarHeader.setText(R.string.test_contrast)
        buttonToolbarLeft.setImageResource(R.drawable.ic_back)
        buttonToolbarLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initOnClickListeners() = binding.apply {
        buttonUp.setOnClickListener {
            presenter.onAnswer(Direction.UP)
        }
        buttonDown.setOnClickListener {
            presenter.onAnswer(Direction.DOWN)
        }
        buttonLeft.setOnClickListener {
            presenter.onAnswer(Direction.LEFT)
        }
        buttonRight.setOnClickListener {
            presenter.onAnswer(Direction.RIGHT)
        }
    }

    override fun populateData(
        direction: Direction,
        backgroundAlpha: Float,
        foregroundDelta: Float,
        progress: Int
    ) {
        binding.apply {
            percentProgressView.progress = progress

            val answerButtons = listOf(
                buttonUp,
                buttonDown,
                buttonLeft,
                buttonRight
            )

            answerButtons.forEach {
                it.isEnabled = false
            }

            layoutTest.animate()
                .alpha(0f)
                .setStartDelay(0)
                .setDuration(FADE_ANIMATION_MS)
                .withEndActionSafe(this@ContrastTestFragment) {
                    imageViewBackground.alpha = backgroundAlpha
                    imageViewForeground.alpha = foregroundDelta
                    val imageRes = when (direction) {
                        Direction.UP -> R.drawable.ic_circle_with_cutout_top
                        Direction.DOWN -> R.drawable.ic_circle_with_cutout_bottom
                        Direction.LEFT -> R.drawable.ic_circle_with_cutout_left
                        Direction.RIGHT -> R.drawable.ic_circle_with_cutout_right
                    }
                    imageViewForeground.setImageResource(imageRes)

                    layoutTest.setVisible()
                    layoutTest.animate()
                        .alpha(1f)
                        .setStartDelay(0)
                        .setDuration(FADE_ANIMATION_MS)
                        .withEndActionSafe(this@ContrastTestFragment) {
                            answerButtons.forEach {
                                it.isEnabled = true
                            }
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
