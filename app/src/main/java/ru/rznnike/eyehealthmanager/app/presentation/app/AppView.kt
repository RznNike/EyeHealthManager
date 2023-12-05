package ru.rznnike.eyehealthmanager.app.presentation.app

import com.github.terrakok.cicerone.Screen
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface AppView : MvpView {
    @OneExecution
    fun routerNewRootFlow(flow: Screen)

    @OneExecution
    fun routerStartSingle(flow: Screen)
}