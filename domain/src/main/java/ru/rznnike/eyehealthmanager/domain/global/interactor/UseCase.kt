package ru.rznnike.eyehealthmanager.domain.global.interactor

import kotlinx.coroutines.withContext
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider

abstract class UseCase<R>(private val dispatcherProvider: DispatcherProvider) {
    suspend operator fun invoke(): UseCaseResult<R> {
        return try {
            withContext(dispatcherProvider.default) {
                UseCaseResult(data = execute())
            }
        } catch (e: Exception) {
            UseCaseResult(error = e)
        }
    }

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(): R
}
