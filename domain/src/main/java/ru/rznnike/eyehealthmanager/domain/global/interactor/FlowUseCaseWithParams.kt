package ru.rznnike.eyehealthmanager.domain.global.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider

abstract class FlowUseCaseWithParams<in P, R>(private val dispatcherProvider: DispatcherProvider) {
    suspend operator fun invoke(parameters: P): Flow<R> {
        return execute(parameters).flowOn(dispatcherProvider.default)
    }

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: P): Flow<R>
}
