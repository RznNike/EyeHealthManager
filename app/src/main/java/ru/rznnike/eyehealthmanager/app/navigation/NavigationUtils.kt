package ru.rznnike.eyehealthmanager.app.navigation

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.BackTo
import com.github.terrakok.cicerone.Command
import com.github.terrakok.cicerone.Forward
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.Replace
import com.github.terrakok.cicerone.Screen
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

fun Navigator.setLaunchScreen(screen: Screen) {
    applyCommands(arrayOf(BackTo(null), Replace(screen)))
}

fun Navigator.setLaunchScreenChain(vararg screens: Screen) {
    val commands = mutableListOf<Command>()
    commands.add(BackTo(null))
    screens.forEachIndexed { index, screen ->
        commands.add(
            if (index == 0) Replace(screen) else Forward(screen)
        )
    }
    applyCommands(commands.toTypedArray())
}

fun KClass<out Fragment>.getFragmentScreen(
    vararg arguments: Pair<String, Any?>,
    clearContainer: Boolean = true
) = FragmentScreenWithArguments(
    arguments = arguments.toMap(),
    screenKey = java.name,
    fragmentCreator = {
        createInstance().apply {
            this.arguments = bundleOf(*arguments)
        }
    },
    clearContainer = clearContainer
)