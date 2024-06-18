package ru.rznnike.eyehealthmanager.app.utils

import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import org.mockito.ArgumentMatchers.floatThat
import org.mockito.kotlin.argThat
import ru.rznnike.eyehealthmanager.app.navigation.FragmentScreenWithArguments
import ru.rznnike.eyehealthmanager.domain.global.CoroutineScopeProvider
import kotlin.math.abs
import kotlin.reflect.KClass

fun CoroutineScope.createTestCoroutineProvider() = object : CoroutineScopeProvider {
    override val ui = this@createTestCoroutineProvider
    override val default = this@createTestCoroutineProvider
    override val io = this@createTestCoroutineProvider
    override val unconfined = this@createTestCoroutineProvider
}

fun screenMatcher(
    fragmentClass: KClass<out Fragment>,
    argumentsValidation: ((Map<String, Any?>) -> Boolean)? = null
) = argThat<FragmentScreenWithArguments> {
    (screenKey == fragmentClass.java.name) && (argumentsValidation?.invoke(arguments) ?: true)
}

fun floatEquals(value: Float, precision: Float = 1e-4f) = floatThat { abs((it ?: 0f) - value) < precision }