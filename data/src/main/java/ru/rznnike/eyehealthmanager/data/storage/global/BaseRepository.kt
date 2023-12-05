package ru.rznnike.eyehealthmanager.data.storage.global

import io.objectbox.Box
import io.objectbox.BoxStore

abstract class BaseRepository<T>(boxStore: BoxStore, typeParameterClass: Class<T>) {
    protected val box: Box<T> = boxStore.boxFor(typeParameterClass)

    protected fun finalize() {
        this.box.closeThreadResources()
    }
}
