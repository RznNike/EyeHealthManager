package ru.rznnike.eyehealthmanager.app.global.presentation

import com.github.terrakok.cicerone.Screen
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface NavigationMvpView : MvpView {
    @OneExecution
    fun routerNavigateTo(screen: Screen)

    @OneExecution
    fun routerNewRootScreen(screen: Screen)

    @OneExecution
    fun routerNewRootChain(vararg screens: Screen)

    @OneExecution
    fun routerReplaceScreen(screen: Screen)

    @OneExecution
    fun routerExit()

    @OneExecution
    fun routerStartFlow(flow: Screen)

    @OneExecution
    fun routerNewRootFlow(flow: Screen)

    @OneExecution
    fun routerReplaceFlow(flow: Screen)

    @OneExecution
    fun routerFinishFlow()

    @OneExecution
    fun routerBackTo(flow: Screen)

    @OneExecution
    fun routerForwardTo(flow: Screen)

    @OneExecution
    fun routerStartSingle(flow: Screen)
}
