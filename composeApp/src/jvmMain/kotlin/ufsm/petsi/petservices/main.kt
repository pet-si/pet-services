package ufsm.petsi.petservices

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ufsm.petsi.petservices.database.DriverFactory
import ufsm.petsi.petservices.database.createDatabase

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "petservices",
    ) {
        App()
    }
}