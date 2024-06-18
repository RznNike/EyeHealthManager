package ru.rznnike.eyehealthmanager.data.utils

import kotlinx.coroutines.CoroutineDispatcher
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider

fun CoroutineDispatcher.createTestDispatcherProvider(): DispatcherProvider = object : DispatcherProvider {
    override val ui = this@createTestDispatcherProvider
    override val default = this@createTestDispatcherProvider
    override val io = this@createTestDispatcherProvider
    override val unconfined = this@createTestDispatcherProvider
}