package ufsm.petsi.petservices

import android.app.Application
import org.koin.android.ext.koin.androidContext
import ufsm.petsi.petservices.di.initializeKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeKoin(
            config = { androidContext(this@MyApplication) }
        )
    }
}