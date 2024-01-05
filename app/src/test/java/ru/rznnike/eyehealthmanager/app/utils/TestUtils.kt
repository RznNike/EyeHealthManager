package ru.rznnike.eyehealthmanager.app.utils

import kotlinx.coroutines.CoroutineScope
import ru.rznnike.eyehealthmanager.domain.global.CoroutineProvider

fun CoroutineScope.createTestCoroutineProvider() = object : CoroutineProvider {
    override val scopeIo = this@createTestCoroutineProvider
    override val scopeMain = this@createTestCoroutineProvider
    override val scopeMainImmediate = this@createTestCoroutineProvider
    override val scopeUnconfined = this@createTestCoroutineProvider
}