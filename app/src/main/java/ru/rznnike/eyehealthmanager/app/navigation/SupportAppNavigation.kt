package ru.rznnike.eyehealthmanager.app.navigation

import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.transition.Fade
import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.app.utils.AppConstants

open class SupportAppNavigation(
    appActivity: AppCompatActivity,
    private val notifier: Notifier,
    containerId: Int
) : AppNavigator(appActivity, appActivity.supportFragmentManager, containerId) {
    private var doubleBackToExitPressedOnce: Boolean = false

    override fun setupFragmentTransaction(
        screen: FragmentScreen,
        fragmentTransaction: FragmentTransaction,
        currentFragment: Fragment?,
        nextFragment: Fragment
    ) {
        fragmentTransaction.setReorderingAllowed(true)

        nextFragment.enterTransition = Fade(Fade.IN)
        currentFragment?.exitTransition = Fade(Fade.OUT)
    }

    override fun activityBack() {
        if (doubleBackToExitPressedOnce) {
            super.activityBack()
            return
        }
        doubleBackToExitPressedOnce = true
        notifier.sendMessage(R.string.double_back_to_exit)
        Handler(Looper.getMainLooper()).postDelayed(
            { doubleBackToExitPressedOnce = false },
            AppConstants.APP_EXIT_DURATION_MS
        )
    }
}