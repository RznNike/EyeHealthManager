package ru.rznnike.eyehealthmanager.domain.global

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val ui: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}