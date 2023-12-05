package ru.rznnike.eyehealthmanager.app.navigation

import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.Screen

class FlowRouter(private val appRouter: AppRouter) : Router() {
    fun startFlow(flow: Screen) = appRouter.navigateTo(flow)

    fun finishFlow() = appRouter.exit()

    fun newRootFlow(flow: Screen) = appRouter.newRootScreen(flow)

    fun replaceFlow(flow: Screen) = appRouter.replaceScreen(flow)

    fun forwardTo(flow: Screen) = appRouter.forwardTo(flow)

    fun startSingle(flow: Screen) = appRouter.startSingle(flow)

    fun toTop(flow: Screen) = appRouter.toTop(flow)
}
