package ru.rznnike.eyehealthmanager.app.global.ui.activity

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import androidx.activity.addCallback
import androidx.annotation.LayoutRes
import com.yariksoffice.lingver.Lingver
import moxy.MvpAppCompatActivity
import org.koin.android.ext.android.inject
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.presentation.ErrorHandler
import ru.rznnike.eyehealthmanager.app.global.ui.fragment.BaseFragment
import ru.rznnike.eyehealthmanager.data.preference.PreferencesWrapper
import ru.rznnike.eyehealthmanager.domain.model.enums.Language
import java.util.*

abstract class BaseActivity(@LayoutRes layoutRes: Int) : MvpAppCompatActivity(layoutRes) {
    private val preferences: PreferencesWrapper by inject()
    private val errorHandler: ErrorHandler by inject()

    private val currentFragment: BaseFragment?
        get() = supportFragmentManager.findFragmentById(R.id.container) as? BaseFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        setLanguage()
        onBackPressedDispatcher.addCallback(this) {
            currentFragment?.onBackPressed()
        }
        super.onCreate(savedInstanceState)
    }

    private fun setLanguage() {
        if (!preferences.language.isSet()) {
            val defaultLanguage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Resources.getSystem().configuration.locales[0].language
            } else {
                Locale.getDefault().language
            }
            preferences.language.set(Language[defaultLanguage].tag)
        }

        Lingver.getInstance().setLocale(application, preferences.language.get())
        errorHandler.resources = baseContext.resources
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        errorHandler.resources = baseContext.resources
    }
}
