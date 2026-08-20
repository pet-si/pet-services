package ufsm.petsi.petservices.ui.util

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val LocalIsDesktop = compositionLocalOf { false }

@Composable
fun ProvideWindowSize(content: @Composable () -> Unit) {
    var isDesktop by remember { mutableStateOf(false) }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        isDesktop = maxWidth >= 1080.dp
    }
    CompositionLocalProvider(LocalIsDesktop provides isDesktop) {
        content()
    }
}

@Composable
fun isDesktopLayout(): Boolean = LocalIsDesktop.current
