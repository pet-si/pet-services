package ufsm.petsi.petservices

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

import ufsm.petsi.petservices.ui.login.LoginScreen
import ufsm.petsi.petservices.ui.navigation.AppNavigation

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        AppNavigation()
    }
}