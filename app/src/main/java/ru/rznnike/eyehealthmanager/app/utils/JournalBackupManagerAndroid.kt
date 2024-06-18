package ru.rznnike.eyehealthmanager.app.utils

import android.content.Context
import android.net.Uri
import ru.rznnike.eyehealthmanager.domain.model.test.TestType
import ru.rznnike.eyehealthmanager.domain.utils.JournalBackupManager

abstract class JournalBackupManagerAndroid : JournalBackupManager {
    var context: Context? = null

    abstract fun getAvailableImportTypes(importFolderUri: Uri): List<TestType>
}