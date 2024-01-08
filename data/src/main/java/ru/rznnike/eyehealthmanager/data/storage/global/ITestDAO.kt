package ru.rznnike.eyehealthmanager.data.storage.global

interface ITestDAO<T> {
    fun get(id: Long): T?

    fun add(entity: T): Long

    fun delete(id: Long): Boolean

    fun deleteAll()
}