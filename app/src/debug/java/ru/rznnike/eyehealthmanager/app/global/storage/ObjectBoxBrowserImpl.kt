package ru.rznnike.eyehealthmanager.app.global.storage

import android.content.Context

import io.objectbox.BoxStore
import io.objectbox.android.Admin

class ObjectBoxBrowserImpl : ObjectBoxBrowser {
    override fun init(boxStore: BoxStore, context: Context) {
        Admin(boxStore).start(context)
    }
}
