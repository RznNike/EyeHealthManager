package ru.rznnike.eyehealthmanager.app.di

import org.koin.dsl.module
import ru.rznnike.eyehealthmanager.data.gateway.AnalysisGatewayImpl
import ru.rznnike.eyehealthmanager.data.gateway.NotificationGatewayImpl
import ru.rznnike.eyehealthmanager.data.gateway.TestGatewayImpl
import ru.rznnike.eyehealthmanager.data.gateway.UserGatewayImpl
import ru.rznnike.eyehealthmanager.domain.gateway.AnalysisGateway
import ru.rznnike.eyehealthmanager.domain.gateway.NotificationGateway
import ru.rznnike.eyehealthmanager.domain.gateway.TestGateway
import ru.rznnike.eyehealthmanager.domain.gateway.UserGateway

val gatewayModule = module {
    single<UserGateway> { UserGatewayImpl(get()) }
    single<NotificationGateway> { NotificationGatewayImpl() }
    single<TestGateway> { TestGatewayImpl(get(), get()) }
    single<AnalysisGateway> { AnalysisGatewayImpl(get()) }
}
