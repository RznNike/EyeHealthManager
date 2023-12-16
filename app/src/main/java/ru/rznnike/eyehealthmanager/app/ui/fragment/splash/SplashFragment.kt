package ru.rznnike.eyehealthmanager.app.ui.fragment.splash

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.presentation.splash.SplashPresenter
import ru.rznnike.eyehealthmanager.app.presentation.splash.SplashView
import ru.rznnike.eyehealthmanager.app.utils.extensions.withDelay
import ru.rznnike.eyehealthmanager.databinding.FragmentSplashBinding

private const val PAUSE_DURATION_MS = 1000L

class SplashFragment : BaseFragment(R.layout.fragment_splash), SplashView {
    @InjectPresenter
    lateinit var presenter: SplashPresenter

    private val binding by viewBinding(FragmentSplashBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startFirstAnimation()
        binding.withDelay(PAUSE_DURATION_MS / 2) {
            activity?.window?.setBackgroundDrawableResource(R.color.colorBackground)
        }
    }

    private fun startFirstAnimation() = binding.apply {
        withDelay(PAUSE_DURATION_MS) {
            presenter.onAnimationEnd()
        }
    }
}
