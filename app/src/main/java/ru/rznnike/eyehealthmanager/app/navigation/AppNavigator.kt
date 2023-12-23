package ru.rznnike.eyehealthmanager.app.navigation

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.github.terrakok.cicerone.*
import com.github.terrakok.cicerone.androidx.*
import com.github.terrakok.cicerone.androidx.AppNavigator

open class AppNavigator(
    activity: FragmentActivity,
    fragmentManager: FragmentManager,
    containerId: Int
) : AppNavigator(activity, containerId, fragmentManager) {
    private val handler = Handler(Looper.getMainLooper())

    override fun applyCommands(vararg commands: Command) {
        try {
            super.applyCommands(commands)
        } catch (error: IllegalStateException) {
            handler.postDelayed({
                applyCommands(*commands)
            }, 100)
        }
    }

    override fun applyCommand(command: Command) {
        when (command) {
            is Forward -> forward(command)
            is Replace -> replace(command)
            is BackTo -> backTo(command)
            is Back -> back()
            is ForwardTo -> activityForwardTo(command)
            is StartSingle -> activityStartSingle(command)
        }
    }

    private fun activityForwardTo(command: ForwardTo) {
        val screen = command.screen
        if ((screen is ActivityScreen) || (!checkTopStackScreen(command.screen))) {
            forward(Forward(screen))
        }
    }

    private fun activityStartSingle(command: StartSingle) {
        val screen = command.screen
        if ((screen is ActivityScreen) || (!checkTopStackScreen(command.screen))) {
            forward(Forward(screen))
        } else {
            replace(Replace(screen))
        }
    }

    private fun checkTopStackScreen(screen: Screen?): Boolean {
        val key = screen?.screenKey
        val index = localStackCopy.indexOfFirst { it == key }
        return index != -1 && index == localStackCopy.lastIndex
    }
}