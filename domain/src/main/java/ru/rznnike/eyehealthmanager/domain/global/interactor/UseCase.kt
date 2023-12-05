package ru.rznnike.eyehealthmanager.domain.global.interactor

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class UseCase<R>(private val coroutineDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(): UseCaseResult<R> {
        return try {
            withContext(coroutineDispatcher) {
                UseCaseResult(data = execute())
            }
        } catch (e: Exception) {
            UseCaseResult(error = e)
        }
    }

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(): R
}
