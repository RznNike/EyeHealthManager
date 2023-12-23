package ru.rznnike.eyehealthmanager.app.navigation

import com.github.terrakok.cicerone.Command
import com.github.terrakok.cicerone.Screen

/**
 * Opens new screen if screen not exists on stack top
 */
class ForwardTo(val screen: Screen) : Command

/**
 * Opens new screen on stack top, replaces similar screen if exists
 */
class StartSingle(val screen: Screen) : Command