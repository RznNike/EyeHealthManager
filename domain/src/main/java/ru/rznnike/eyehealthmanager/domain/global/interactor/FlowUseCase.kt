package ru.rznnike.eyehealthmanager.domain.global.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.rznnike.eyehealthmanager.domain.global.DispatcherProvider

abstract class FlowUseCase<R>(private val dispatcherProvider: DispatcherProvider) {
    suspend operator fun invoke(): Flow<R> {
        return execute().flowOn(dispatcherProvider.default)
    }

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(): Flow<R>
}
