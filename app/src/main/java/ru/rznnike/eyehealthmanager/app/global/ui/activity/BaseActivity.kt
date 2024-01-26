package ru.rznnike.eyehealthmanager.app.global.ui.activity

import android.content.Context
import android.os.Bundle
import androidx.activity.addCallback
import androidx.annotation.LayoutRes
import moxy.MvpAppCompatActivity
import org.koin.android.ext.android.inject
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.app.utils.extensions.applyTheme
import ru.rznnike.eyehealthmanager.data.preference.PreferencesWrapper
import ru.rznnike.eyehealthmanager.domain.model.enums.AppTheme

abstract class BaseActivity(@LayoutRes layoutRes: Int) : MvpAppCompatActivity(layoutRes) {
    private val preferences: PreferencesWrapper by inject()
    private val errorHandler: ErrorHandler by inject()

    private val currentFragment: BaseFragment?
        get() = supportFragmentManager.findFragmentById(R.id.container) as? BaseFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        applyTheme(AppTheme[preferences.appTheme.get()])
        onBackPressedDispatcher.addCallback(this) {
            currentFragment?.onBackPressed()
        }
        super.onCreate(savedInstanceState)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        errorHandler.resources = baseContext.resources
    }
}
