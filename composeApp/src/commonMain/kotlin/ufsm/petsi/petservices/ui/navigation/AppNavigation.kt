package ufsm.petsi.petservices.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ufsm.petsi.petservices.ui.home.HomeScreen
import ufsm.petsi.petservices.ui.login.LoginScreen
import ufsm.petsi.petservices.ui.signup.SignupScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LoginRoute,
    ) {
        composable<LoginRoute>{
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(HomeRoute(it.idUser)) {
                        popUpTo<LoginRoute> {
                            inclusive = true
                        }
                    }
                },
                onNavigateToSignup = {
                    navController.navigate(SignupRoute)
                }
            )
        }
        composable<SignupRoute>{
            SignupScreen(onNavigateBack = {
                navController.popBackStack()
            })
        }
        composable<HomeRoute> { backStackEntry ->
            val homeRoute = backStackEntry.toRoute<HomeRoute>()
            HomeScreen(homeRoute.userId)
        }
    }
}