package ufsm.petsi.petservices

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import ufsm.petsi.petservices.ui.navigation.AppNavigation
import ufsm.petsi.petservices.util.LocalWindowSize
import ufsm.petsi.petservices.util.getWindowSize


@Composable
@Preview
fun App() {
    MaterialTheme {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val windowSize = getWindowSize(maxWidth)
            CompositionLocalProvider(LocalWindowSize provides windowSize) {
                AppNavigation()
            }
        }
    }
}