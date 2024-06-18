package ru.rznnike.eyehealthmanager.domain.global.interactor

import kotlinx.coroutines.withContext
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider

abstract class UseCaseWithParams<in P, R>(private val dispatcherProvider: DispatcherProvider) {
    suspend operator fun invoke(parameters: P): UseCaseResult<R> {
        return try {
            withContext(dispatcherProvider.default) {
                UseCaseResult(data = execute(parameters))
            }
        } catch (e: Exception) {
            UseCaseResult(error = e)
        }
    }

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: P): R
}
