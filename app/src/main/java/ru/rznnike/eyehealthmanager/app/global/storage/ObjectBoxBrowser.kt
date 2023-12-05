package ru.rznnike.eyehealthmanager.app.global.storage

import android.content.Context
import io.objectbox.BoxStore

interface ObjectBoxBrowser {
    fun init(boxStore: BoxStore, context: Context)
}