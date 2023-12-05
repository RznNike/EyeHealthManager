package ru.rznnike.eyehealthmanager.app.utils.extensions

import android.app.Activity
import android.content.Intent
import kotlin.system.exitProcess

fun Activity.restartApp() {
    val intent = Intent(this, this::class.java)
    val restartIntent = Intent.makeRestartActivityTask(intent.component)
    startActivity(restartIntent)
    exitProcess(0)
}
