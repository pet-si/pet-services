package ufsm.petsi.petservices.ui.navigation.navigationBar

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import kotlin.reflect.KClass

@Composable
fun BottomNavigationBar(
    currentDestination: NavDestination?,
    onSelectKey: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        modifier = modifier
    ) {
        TOP_LEVEL_DESTINATIONS.forEach { (route, item) ->
            val isSelected = currentDestination?.hierarchy?.any { destination ->
                // Remove argumentos para ter o nome da rota
                val baseRoute = destination.route?.substringBefore("?")?.substringBefore("/")
                baseRoute == route::class.qualifiedName
            } == true
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelectKey(route) },
                icon = { Icon( imageVector = item.icon, contentDescription = item.label )},
                label = { Text(item.label) }
            )
        }
    }
}