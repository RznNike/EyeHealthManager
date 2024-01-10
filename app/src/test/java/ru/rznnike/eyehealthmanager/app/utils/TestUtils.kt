package ru.rznnike.eyehealthmanager.app.utils

import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import org.mockito.kotlin.argThat
import ru.rznnike.eyehealthmanager.app.navigation.FragmentScreenWithArguments
import ru.rznnike.eyehealthmanager.domain.global.CoroutineProvider
import kotlin.reflect.KClass

fun CoroutineScope.createTestCoroutineProvider() = object : CoroutineProvider {
    override val scopeIo = this@createTestCoroutineProvider
    override val scopeMain = this@createTestCoroutineProvider
    override val scopeMainImmediate = this@createTestCoroutineProvider
    override val scopeUnconfined = this@createTestCoroutineProvider
}

fun screenMatcher(
    fragmentClass: KClass<out Fragment>,
    argumentsValidation: ((Map<String, Any?>) -> Boolean)? = null
) = argThat<FragmentScreenWithArguments> {
    (screenKey == fragmentClass.java.name) && (argumentsValidation?.invoke(arguments) ?: true)
}