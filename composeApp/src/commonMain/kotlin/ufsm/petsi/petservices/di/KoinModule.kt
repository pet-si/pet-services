package ufsm.petsi.petservices.di

import app.cash.sqldelight.db.SqlDriver
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ufsm.petsi.petservices.database.AppDatabase
import ufsm.petsi.petservices.database.DriverFactory
import ufsm.petsi.petservices.repository.implementations.ProductRepository
import ufsm.petsi.petservices.repository.implementations.UserRepository
import ufsm.petsi.petservices.ui.login.LoginViewModel
import ufsm.petsi.petservices.ui.signup.SignupViewModel

expect val targetModule: Module

val sharedModule = module {
    single<SqlDriver> { get<DriverFactory>().createDriver() }
    single<AppDatabase> { AppDatabase(get()) }
    single<UserRepository> { UserRepository(get()) }
    single<ProductRepository> { ProductRepository(get()) }

    viewModelOf(::LoginViewModel)
    viewModelOf(::SignupViewModel)
}

fun initializeKoin(config: (KoinApplication.() -> Unit)? = null) {
    startKoin {
        config?.invoke(this)
        modules(targetModule, sharedModule)
    }
}