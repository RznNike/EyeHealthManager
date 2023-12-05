package ru.rznnike.eyehealthmanager.app.navigation

import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.NavigatorHolder

open class FlowNavigationViewModel(private val appRouter: AppRouter) : ViewModel() {
    private val flowCicerone: Cicerone<FlowRouter> by lazy {
        Cicerone.create(FlowRouter(appRouter))
    }

    val router: FlowRouter
        get() = flowCicerone.router

    val navigatorHolder: NavigatorHolder
        get() = flowCicerone.getNavigatorHolder()
}
