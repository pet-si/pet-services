package ufsm.petsi.petservices

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ufsm.petsi.petservices.database.DriverFactory
import ufsm.petsi.petservices.database.createDatabase
import ufsm.petsi.petservices.di.initializeKoin

fun main() = application {
    initializeKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "petservices",
    ) {
        App()
    }
}