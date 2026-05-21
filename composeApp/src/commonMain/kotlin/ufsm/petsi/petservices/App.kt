package ufsm.petsi.petservices

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

import ufsm.petsi.petservices.ui.navigation.AppNavigation


@Composable
@Preview
fun App() {
    MaterialTheme {
        AppNavigation()
    }
}