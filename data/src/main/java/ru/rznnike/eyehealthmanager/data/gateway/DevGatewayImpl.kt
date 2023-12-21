package ru.rznnike.eyehealthmanager.data.gateway

import ru.rznnike.eyehealthmanager.data.storage.repository.TestRepository
import ru.rznnike.eyehealthmanager.domain.gateway.DevGateway
import ru.rznnike.eyehealthmanager.domain.model.enums.DataGenerationType

class DevGatewayImpl(
    private val testRepository: TestRepository
) : DevGateway {
    override suspend fun generateData(type: DataGenerationType) {
        // TODO
    }
}