package ru.rznnike.eyehealthmanager.app.global.ui.fragment

import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.Screen
import moxy.MvpAppCompatFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel
import ru.rznnike.eyehealthmanager.app.navigation.FlowNavigationViewModel
import ru.rznnike.eyehealthmanager.app.utils.extensions.hideKeyboard
import java.util.*
import kotlin.concurrent.schedule

abstract class BaseFragment(@LayoutRes layoutRes: Int) : MvpAppCompatFragment(layoutRes) {
    open var isLightStatusBar: Boolean = true
        protected set
    open var isLightNavigationBar: Boolean = true
        protected set
    open var progressDelayMs: Long = DEFAULT_PROGRESS_DELAY_MS
        protected set
    open var progressCallback: ((show: Boolean) -> Unit)? = null
        protected set
    private var progressTask: TimerTask? = null
    private var goalProgressIsVisible = false

    private val flowParent
        get() = this as? FlowFragment ?: getParent(this)

    val navigation: FlowNavigationViewModel by lazy { getViewModel(ownerProducer = { flowParent }) }

    protected val router by lazy { navigation.router }

    private fun getParent(fragment: Fragment): FlowFragment {
        return when {
            fragment is FlowFragment -> fragment
            fragment.parentFragment == null ->
                throw IllegalStateException("Fragment must have FlowFragment or Activity parent")
            else -> getParent(fragment.requireParentFragment())
        }
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    open fun onBackPressed() {
        router.exit()
    }

    override fun onResume() {
        super.onResume()
        initStatusAndNavigationBar()
    }

    private fun initStatusAndNavigationBar() {
        if (this is FlowFragment || childFragmentManager.fragments.isNotEmpty()) {
            return
        }
        updateSystemBarsColors()
    }

    fun updateSystemBarsColors() {
        applyStatusBarMode()
        applyNavigationBarMode()
    }

    fun setStatusBarMode(isLightStatusBar: Boolean) {
        this.isLightStatusBar = isLightStatusBar
        applyStatusBarMode()
    }

    private fun applyStatusBarMode() {
        if (Build.VERSION.SDK_INT >= 30) {
            val flag = if (isLightStatusBar) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0
            activity?.window?.insetsController?.setSystemBarsAppearance(
                flag,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            activity?.apply {
                var flags = window.decorView.systemUiVisibility

                flags = if (isLightStatusBar) {
                    flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }

                window.decorView.systemUiVisibility = flags
            }
        }
    }

    private fun applyNavigationBarMode() {
        when {
            Build.VERSION.SDK_INT >= 30 -> {
                val flag = if (isLightNavigationBar) WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS else 0
                activity?.window?.insetsController?.setSystemBarsAppearance(
                    flag,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                )
            }
            Build.VERSION.SDK_INT >= 26 -> {
                @Suppress("DEPRECATION")
                activity?.apply {
                    var flags = window.decorView.systemUiVisibility
                    flags = if (isLightNavigationBar) {
                        flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    } else {
                        flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                    }
                    window.decorView.systemUiVisibility = flags
                }
            }
        }
    }

    fun setProgress(show: Boolean) = setProgress(show, false)

    fun setProgress(show: Boolean, immediately: Boolean) {
        if ((goalProgressIsVisible == show) && (progressTask != null)) return

        goalProgressIsVisible = show
        progressTask?.cancel()
        if (immediately || (progressDelayMs <= 0)) {
            view?.post {
                progressCallback?.invoke(show)
            }
            progressTask = null
        } else {
            progressTask = Timer().schedule(progressDelayMs) {
                view?.post {
                    progressCallback?.invoke(show)
                }
                progressTask = null
            }
        }
    }

    fun routerNavigateTo(screen: Screen) = router.navigateTo(screen)

    fun routerNewRootScreen(screen: Screen) = router.newRootScreen(screen)

    fun routerNewRootFlow(flow: Screen) = router.newRootFlow(flow)

    fun routerNewRootChain(vararg screens: Screen) = router.newRootChain(*screens)

    fun routerNewChain(vararg screens: Screen) = router.newChain(*screens)

    fun routerReplaceScreen(screen: Screen) = router.replaceScreen(screen)

    fun routerExit() = router.exit()

    fun routerStartFlow(flow: Screen) = router.startFlow(flow)

    fun routerReplaceFlow(flow: Screen) = router.replaceFlow(flow)

    fun routerFinishFlow() = router.finishFlow()

    fun routerBackTo(flow: Screen) = router.backTo(flow)

    fun routerForwardTo(flow: Screen) = router.forwardTo(flow)

    fun routerStartSingle(flow: Screen) = router.startSingle(flow)

    fun routerToTop(flow: Screen) = router.toTop(flow)

    companion object {
        const val DEFAULT_PROGRESS_DELAY_MS = 250L
    }
}
