package ru.rznnike.eyehealthmanager.domain.global

import kotlinx.coroutines.CoroutineScope

interface CoroutineScopeProvider {
    val ui: CoroutineScope
    val default: CoroutineScope
    val io: CoroutineScope
    val unconfined: CoroutineScope
}
