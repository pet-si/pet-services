package ufsm.petsi.petservices.di

import org.koin.core.module.Module
import org.koin.dsl.module
import ufsm.petsi.petservices.database.DriverFactory

actual val targetModule = module {
    single<DriverFactory> { DriverFactory() }
}