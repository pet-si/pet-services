package ufsm.petsi.petservices.ui.navigation.navigationBar

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.outlined.Home
import com.composables.icons.materialicons.outlined.Inventory
import ufsm.petsi.petservices.ui.navigation.HomeRoute
import ufsm.petsi.petservices.ui.navigation.ProductRoute

data class NavigationItem(
    val icon : ImageVector,
    val label : String
)

val TOP_LEVEL_DESTINATIONS = mapOf(
    HomeRoute to NavigationItem(
        icon = MaterialIcons.Outlined.Home,
        label = "Início"
    ),
    ProductRoute to NavigationItem(
        icon = MaterialIcons.Outlined.Inventory,
        label = "Estoque"
    )
)