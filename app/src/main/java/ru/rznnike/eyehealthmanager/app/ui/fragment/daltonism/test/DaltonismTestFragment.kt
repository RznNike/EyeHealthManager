package ru.rznnike.eyehealthmanager.app.ui.fragment.daltonism.test

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.annotation.DrawableRes
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import coil.transform.RoundedCornersTransformation
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogAction
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogParameters
import ru.rznnike.eyehealthmanager.app.dialog.showAlertDialog
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.daltonism.test.DaltonismTestPresenter
import ru.rznnike.eyehealthmanager.app.presentation.daltonism.test.DaltonismTestView
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.app.utils.extensions.setVisible
import ru.rznnike.eyehealthmanager.app.utils.extensions.withEndActionSafe
import ru.rznnike.eyehealthmanager.app.utils.extensions.withStartActionSafe
import ru.rznnike.eyehealthmanager.databinding.FragmentDaltonismTestBinding

private const val FADE_ANIMATION_MS = 500L
private const val IMAGE_CORNERS_DP = 16f

class DaltonismTestFragment : BaseFragment(R.layout.fragment_daltonism_test), DaltonismTestView {
    @InjectPresenter
    lateinit var presenter: DaltonismTestPresenter

    private val binding by viewBinding(FragmentDaltonismTestBinding::bind)

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
        textViewToolbarHeader.setText(R.string.test_daltonism)
        buttonToolbarLeft.setImageResource(R.drawable.ic_back)
        buttonToolbarLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initOnClickListeners() = binding.apply {
        listOf(
            buttonVariant1,
            buttonVariant2,
            buttonVariant3,
            buttonVariant4
        ).forEachIndexed { index, button ->
            button.setOnClickListener {
                presenter.onAnswer(index)
            }
        }
    }

    override fun populateData(@DrawableRes imageResId: Int, variants: List<Int>, progress: Int) {
        binding.apply {
            percentProgressView.progress = progress

            val answerButtons = listOf(
                buttonVariant1,
                buttonVariant2,
                buttonVariant3,
                buttonVariant4
            )
            answerButtons.forEach {
                it.isEnabled = false
            }

            layoutTest.animate()
                .alpha(0f)
                .setStartDelay(0)
                .setDuration(FADE_ANIMATION_MS)
                .withStartActionSafe(this@DaltonismTestFragment) {
                    layoutControls.animate()
                        .alpha(0f)
                        .setStartDelay(0)
                        .setDuration(FADE_ANIMATION_MS)
                        .start()
                }
                .withEndActionSafe(this@DaltonismTestFragment) {
                    answerButtons.forEachIndexed { index, button ->
                        button.setText(variants[index])
                    }

                    val cornersPx = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        IMAGE_CORNERS_DP,
                        resources.displayMetrics
                    )
                    val width = imageViewTest.width
                    val height = imageViewTest.height
                    if ((width > 0) && (height > 0)) {
                        imageViewTest.load(imageResId) {
                            size(
                                width = imageViewTest.width,
                                height = imageViewTest.height
                            )
                            transformations(
                                RoundedCornersTransformation(
                                    topLeft = cornersPx,
                                    topRight = cornersPx,
                                    bottomLeft = cornersPx,
                                    bottomRight = cornersPx
                                )
                            )
                        }
                    }

                    layoutControls.setVisible()
                    layoutTest.setVisible()
                    layoutTest.animate()
                        .alpha(1f)
                        .setStartDelay(0)
                        .setDuration(FADE_ANIMATION_MS)
                        .withEndActionSafe(this@DaltonismTestFragment) {
                            layoutControls.animate()
                                .alpha(1f)
                                .setStartDelay(0)
                                .setDuration(FADE_ANIMATION_MS)
                                .withEndActionSafe(this@DaltonismTestFragment) {
                                    answerButtons.forEach {
                                        it.isEnabled = true
                                    }
                                }
                                .start()
                        }
                        .start()
                }
                .start()
        }
    }

    private fun showExitDialog() {
        showAlertDialog(
            parameters = AlertDialogParameters.HORIZONTAL_2_OPTIONS_LEFT_ACCENT,
            header = getString(R.string.test_cancel_header),
            message = getString(R.string.test_cancel_message),
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
