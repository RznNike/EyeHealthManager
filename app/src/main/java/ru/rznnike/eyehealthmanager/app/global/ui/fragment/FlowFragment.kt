package ru.rznnike.eyehealthmanager.app.global.ui.fragment

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.transition.Fade
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import org.koin.android.ext.android.inject
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.crash.CrashlyticsProvider
import ru.rznnike.eyehealthmanager.app.navigation.AppNavigator
import ru.rznnike.eyehealthmanager.app.navigation.setLaunchScreen

abstract class FlowFragment(@LayoutRes layoutRes: Int = R.layout.layout_container) : BaseFragment(layoutRes) {
    private val crashlyticsProvider: CrashlyticsProvider by inject()

    val currentFragment
        get() = childFragmentManager.findFragmentById(R.id.container) as? BaseFragment

    open val launchScreen: Screen? = null

    private val navigator: AppNavigator by lazy {
        object : AppNavigator(requireActivity(), childFragmentManager, R.id.container) {
            override fun setupFragmentTransaction(
                screen: FragmentScreen,
                fragmentTransaction: FragmentTransaction,
                currentFragment: Fragment?,
                nextFragment: Fragment
            ) {
                crashlyticsProvider.setCustomKey("Screen", nextFragment.toString())
                // fix incorrect order lifecycle callback of MainFlowFragment
                fragmentTransaction.setReorderingAllowed(true)

                nextFragment.enterTransition = Fade(Fade.IN)
                currentFragment?.exitTransition = Fade(Fade.OUT)
            }

            override fun activityBack() {
                router.finishFlow()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (childFragmentManager.fragments.isEmpty()) {
            launchScreen?.let(navigator::setLaunchScreen)
        }
    }

    override fun onBackPressed() {
        currentFragment?.onBackPressed() ?: router.exit()
    }

    override fun onResume() {
        super.onResume()
        navigation.navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigation.navigatorHolder.removeNavigator()
        super.onPause()
    }
}
