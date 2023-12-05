package ru.rznnike.eyehealthmanager.app.global.presentation

import moxy.MvpPresenter
import moxy.MvpView
import org.koin.core.component.KoinComponent

open class BasePresenter<View : MvpView> : MvpPresenter<View>(), KoinComponent
