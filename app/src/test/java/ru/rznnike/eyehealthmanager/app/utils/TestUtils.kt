package ru.rznnike.eyehealthmanager.app.utils

import com.github.terrakok.cicerone.androidx.FragmentScreen
import kotlinx.coroutines.CoroutineScope
import org.mockito.kotlin.argForWhich
import ru.rznnike.eyehealthmanager.domain.global.CoroutineProvider

fun CoroutineScope.createTestCoroutineProvider() = object : CoroutineProvider {
    override val scopeIo = this@createTestCoroutineProvider
    override val scopeMain = this@createTestCoroutineProvider
    override val scopeMainImmediate = this@createTestCoroutineProvider
    override val scopeUnconfined = this@createTestCoroutineProvider
}

fun screenMatcher(sampleScreen: FragmentScreen) = argForWhich<FragmentScreen> {
    (screenKey == sampleScreen.screenKey)
}