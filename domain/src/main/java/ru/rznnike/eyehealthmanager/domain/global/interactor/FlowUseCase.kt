package ru.rznnike.eyehealthmanager.domain.global.interactor

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

abstract class FlowUseCase<R>(private val coroutineDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(): Flow<R> {
        return execute().flowOn(coroutineDispatcher)
    }

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(): Flow<R>
}
