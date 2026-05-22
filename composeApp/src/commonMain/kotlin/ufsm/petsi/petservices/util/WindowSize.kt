package ufsm.petsi.petservices.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowSize {
    COMPACT,
    EXPANDED
}

fun getWindowSize(width : Dp) : WindowSize = when {
    width <= 850.dp -> WindowSize.COMPACT
    else -> WindowSize.EXPANDED
}

val LocalWindowSize = compositionLocalOf { WindowSize.COMPACT }

val isWideScreen: Boolean
    @Composable
    get() = LocalWindowSize.current == WindowSize.EXPANDED