package ru.rznnike.eyehealthmanager.data.storage.global

import io.objectbox.Box
import io.objectbox.BoxStore
import kotlin.reflect.KClass

abstract class BaseDAO<T : Any>(boxStore: BoxStore, typeParameterClass: KClass<T>) {
    protected val box: Box<T> = boxStore.boxFor(typeParameterClass.java)

    protected fun finalize() {
        box.closeThreadResources()
    }
}
