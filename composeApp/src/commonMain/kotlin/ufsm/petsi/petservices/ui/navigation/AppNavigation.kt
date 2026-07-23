package ufsm.petsi.petservices.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import org.koin.compose.viewmodel.koinViewModel
import ufsm.petsi.petservices.ui.home.HomeScreen
import ufsm.petsi.petservices.ui.login.LoginScreen
import ufsm.petsi.petservices.ui.navigation.navigationBar.BottomNavigationBar
import ufsm.petsi.petservices.ui.products.ProductsScreen
import ufsm.petsi.petservices.ui.products.ProductsViewModel
import ufsm.petsi.petservices.ui.signup.SignupScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showNavigationBar =
        (currentDestination?.route != LoginRoute::class.qualifiedName && currentDestination?.route != SignupRoute::class.qualifiedName)

    Scaffold(
        bottomBar = {
            AnimatedVisibility(visible = showNavigationBar) {
                if (showNavigationBar)
                    BottomNavigationBar(
                        currentDestination = currentDestination,
                        onSelectKey = {
                            navController.navigate(it) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            startDestination = LoginRoute,
        ) {
            composable<LoginRoute> {
                LoginScreen(
                    onNavigateToHome = {
                        navController.popBackStack()
                        navController.navigate(HomeRoute)
                    },
                    onNavigateToSignup = {
                        navController.navigate(SignupRoute)
                    }
                )
            }
            composable<SignupRoute> {
                SignupScreen(onNavigateBack = {
                    navController.popBackStack()
                })
            }

            navigation<MainGraph>(startDestination = HomeRoute) {
                composable<HomeRoute> {
                    HomeScreen()
                }
                composable<ProductRoute> {
                    ProductsScreen()
                }
            }

        }
    }
}