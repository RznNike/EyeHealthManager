package ru.rznnike.eyehealthmanager.app.ui.fragment.nearfar.test

import android.os.Bundle
import android.view.View
import dev.androidbroadcast.vbpd.viewBinding
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogAction
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogParameters
import ru.rznnike.eyehealthmanager.app.dialog.showAlertDialog
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.nearfar.test.NearFarTestPresenter
import ru.rznnike.eyehealthmanager.app.presentation.nearfar.test.NearFarTestView
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.databinding.FragmentNearFarTestBinding

class NearFarTestFragment : BaseFragment(R.layout.fragment_near_far_test), NearFarTestView {
    @InjectPresenter
    lateinit var presenter: NearFarTestPresenter

    private val binding by viewBinding(FragmentNearFarTestBinding::bind)

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
        textViewToolbarHeader.setText(R.string.test_nearsightedness_farsightedness)
        buttonToolbarLeft.setImageResource(R.drawable.ic_back)
        buttonToolbarLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initOnClickListeners() = binding.apply {
        buttonNext.setOnClickListener {
            presenter.openAnswerForm()
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
