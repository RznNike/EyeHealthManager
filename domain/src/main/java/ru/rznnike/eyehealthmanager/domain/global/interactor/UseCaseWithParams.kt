package ru.rznnike.eyehealthmanager.domain.global.interactor

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class UseCaseWithParams<in P, R>(private val coroutineDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(parameters: P): UseCaseResult<R> {
        return try {
            withContext(coroutineDispatcher) {
                UseCaseResult(data = execute(parameters))
            }
        } catch (e: Exception) {
            UseCaseResult(error = e)
        }
    }

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: P): R
}
