package ufsm.petsi.petservices.ui.navigation.navigationBar

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy

@Composable
fun SideNavigationRail(
    currentDestination: NavDestination?,
    onSelectKey: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        TOP_LEVEL_DESTINATIONS.forEach { (route, item) ->
            val isSelected = currentDestination?.hierarchy?.any { destination ->
                val baseRoute = destination.route?.substringBefore("?")?.substringBefore("/")
                baseRoute == route::class.qualifiedName
            } == true
            NavigationRailItem(
                selected = isSelected,
                onClick = { onSelectKey(route) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
