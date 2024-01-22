package ru.rznnike.eyehealthmanager.app.utils.extensions

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import ru.rznnike.eyehealthmanager.domain.model.enums.AppTheme
import kotlin.system.exitProcess

fun Activity.restartApp() {
    val intent = Intent(this, this::class.java)
    val restartIntent = Intent.makeRestartActivityTask(intent.component)
    startActivity(restartIntent)
    exitProcess(0)
}

fun applyTheme(theme: AppTheme) {
    val flag = when (theme) {
        AppTheme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        AppTheme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
        AppTheme.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
    AppCompatDelegate.setDefaultNightMode(flag)
}
