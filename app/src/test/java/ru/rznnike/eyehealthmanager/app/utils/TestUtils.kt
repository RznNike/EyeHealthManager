package ru.rznnike.eyehealthmanager.app.utils

import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import org.mockito.ArgumentMatchers.floatThat
import org.mockito.kotlin.argThat
import ru.rznnike.eyehealthmanager.app.navigation.FragmentScreenWithArguments
import ru.rznnike.eyehealthmanager.domain.global.CoroutineScopeProvider
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider
import kotlin.math.abs
import kotlin.reflect.KClass

fun CoroutineScope.createTestCoroutineProvider() = object : CoroutineScopeProvider {
    override val ui = this@createTestCoroutineProvider
    override val default = this@createTestCoroutineProvider
    override val io = this@createTestCoroutineProvider
    override val unconfined = this@createTestCoroutineProvider
}

fun CoroutineDispatcher.createTestDispatcherProvider(): DispatcherProvider = object :
    DispatcherProvider {
    override val ui = this@createTestDispatcherProvider
    override val default = this@createTestDispatcherProvider
    override val io = this@createTestDispatcherProvider
    override val unconfined = this@createTestDispatcherProvider
}

fun screenMatcher(
    fragmentClass: KClass<out Fragment>,
    argumentsValidation: ((Map<String, Any?>) -> Boolean)? = null
) = argThat<FragmentScreenWithArguments> {
    (screenKey == fragmentClass.java.name) && (argumentsValidation?.invoke(arguments) != false)
}

fun floatEquals(value: Float, precision: Float = 1e-4f) = floatThat { abs((it ?: 0f) - value) < precision }