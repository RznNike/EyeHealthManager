package ru.rznnike.eyehealthmanager.app.ui.fragment.astigmatism.test

import android.os.Bundle
import android.view.View
import androidx.core.view.updateLayoutParams
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogAction
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogParameters
import ru.rznnike.eyehealthmanager.app.dialog.showAlertDialog
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.astigmatism.test.AstigmatismTestPresenter
import ru.rznnike.eyehealthmanager.app.presentation.astigmatism.test.AstigmatismTestView
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.app.utils.extensions.context
import ru.rznnike.eyehealthmanager.app.utils.extensions.convertMmToPx
import ru.rznnike.eyehealthmanager.databinding.FragmentAstigmatismTestBinding
import ru.rznnike.eyehealthmanager.domain.model.test.TestingSettings

private const val BASIC_HEIGHT_MM = 100f
private const val BASIC_DISTANCE_MM = 5000f

class AstigmatismTestFragment : BaseFragment(R.layout.fragment_astigmatism_test), AstigmatismTestView {
    @InjectPresenter
    lateinit var presenter: AstigmatismTestPresenter

    private val binding by viewBinding(FragmentAstigmatismTestBinding::bind)

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
        textViewToolbarHeader.setText(R.string.test_astigmatism)
        buttonToolbarLeft.setImageResource(R.drawable.ic_back)
        buttonToolbarLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initOnClickListeners() = binding.apply {
        buttonNext.setOnClickListener {
            presenter.openAnswer()
        }
    }

    override fun setScale(settings: TestingSettings) {
        binding.apply {
            val finalDpmm = if (settings.dpmm > 0) settings.dpmm else context.convertMmToPx(1f)
            val heightMm = BASIC_HEIGHT_MM * settings.armsLength / BASIC_DISTANCE_MM
            val heightPx = heightMm * finalDpmm
            imageViewTest.updateLayoutParams {
                height = heightPx.toInt()
            }
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
