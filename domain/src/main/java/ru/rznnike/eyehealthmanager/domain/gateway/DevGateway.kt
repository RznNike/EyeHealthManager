package ru.rznnike.eyehealthmanager.domain.gateway

import ru.rznnike.eyehealthmanager.domain.model.common.DataGenerationType

interface DevGateway {
    suspend fun generateData(type: DataGenerationType)
}
