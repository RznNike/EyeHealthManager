package ru.rznnike.eyehealthmanager.app.global.presentation

import moxy.viewstate.strategy.alias.AddToEndSingle

interface PagerView : NavigationMvpView {
    @AddToEndSingle
    fun showProgress(show: Boolean, isRefresh: Boolean, isDataEmpty: Boolean)

    @AddToEndSingle
    fun showErrorView(show: Boolean, message: String? = null)
}
