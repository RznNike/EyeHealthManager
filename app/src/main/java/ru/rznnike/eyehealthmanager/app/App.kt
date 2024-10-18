package ru.rznnike.eyehealthmanager.app

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.FirebaseApp
import io.objectbox.BoxStore
import io.objectbox.android.Admin
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import ru.rznnike.eyehealthmanager.BuildConfig
import ru.rznnike.eyehealthmanager.app.di.appComponent
import ru.rznnike.eyehealthmanager.app.observer.AppLifeCycleObserver

class App : Application() {
    private val appLifecycleObserver: AppLifeCycleObserver by inject()
    private val boxStore: BoxStore by inject()

    override fun onCreate() {
        super.onCreate()

        initFirebase()
        initKoin()
        initLifecycleObserver()
        initObjectBoxBrowser()
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@App)
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            modules(appComponent)
            fragmentFactory()
        }
    }

    private fun initLifecycleObserver() {
        ProcessLifecycleOwner
            .get()
            .lifecycle
            .addObserver(appLifecycleObserver)
    }

    private fun initObjectBoxBrowser() {
        if (BuildConfig.DEBUG) {
            Admin(boxStore).start(this)
        }
    }
}
