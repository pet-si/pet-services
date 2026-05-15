package ufsm.petsi.petservices.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import ufsm.petsi.petservices.database.AppDatabase

expect val targetModule : Module

val sharedModule = module {
    single<AppDatabase> { AppDatabase(get() ) }
}

fun initializeKoin(config : (KoinApplication.() -> Unit)? = null) {
    startKoin {
        config?.invoke(this)
        modules(targetModule, sharedModule)
    }
}