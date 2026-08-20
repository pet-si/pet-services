package ufsm.petsi.petservices.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import ufsm.petsi.petservices.ui.home.HomeScreen
import ufsm.petsi.petservices.ui.login.LoginScreen
import ufsm.petsi.petservices.ui.navigation.navigationBar.BottomNavigationBar
import ufsm.petsi.petservices.ui.navigation.navigationBar.SideNavigationRail
import ufsm.petsi.petservices.ui.products.DesktopProductLayout
import ufsm.petsi.petservices.ui.products.ProductsScreen
import ufsm.petsi.petservices.ui.products.create.CreateProductScreen
import ufsm.petsi.petservices.ui.products.view.ViewProductScreen
import ufsm.petsi.petservices.ui.signup.SignupScreen
import ufsm.petsi.petservices.ui.util.ProvideWindowSize
import ufsm.petsi.petservices.ui.util.isDesktopLayout


@Composable
fun AppNavigation() {
    ProvideWindowSize {
        val navController = rememberNavController()

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val currentRoute = currentDestination?.route ?: ""
        val isDesktop = isDesktopLayout()

        val showNavigation = currentRoute.let { route ->
            route != LoginRoute::class.qualifiedName &&
                    route != SignupRoute::class.qualifiedName &&
                    !route.contains("ViewProductRoute") &&
                    !route.contains("CreateProductRoute")
        }

        Scaffold(
            bottomBar = {
                if (!isDesktop) {
                    AnimatedVisibility(visible = showNavigation) {
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
            }
        ) { innerPadding ->
            if (isDesktop) {
                Row(modifier = Modifier.padding(innerPadding)) {
                    if (showNavigation) {
                        SideNavigationRail(
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
                    NavHost(
                        navController = navController,
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
                                DesktopProductLayout()
                            }

                            composable<ViewProductRoute> {
                                val productId = it.toRoute<ViewProductRoute>().productId
                                ViewProductScreen(
                                    onNavigateBack = { navController.popBackStack() },
                                    onNavigateToEdit = { id ->
                                        navController.navigate(CreateProductRoute(productId = id))
                                    },
                                    productId = productId
                                )
                            }

                            composable<CreateProductRoute> {
                                val productId = it.toRoute<CreateProductRoute>().productId
                                CreateProductScreen(
                                    onNavigateBack = { navController.popBackStack() },
                                    onNavigateToViewProduct = { id ->
                                        navController.popBackStack()
                                        navController.navigate(ViewProductRoute(productId = id))
                                    },
                                    productId = productId
                                )
                            }
                        }
                    }
                }
            } else {
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
                            ProductsScreen(
                                onCreateProduct = {
                                    navController.navigate(CreateProductRoute())
                                },
                                onProductSelected = { productId ->
                                    navController.navigate(ViewProductRoute(productId = productId))
                                },
                                onProductEdit = { productId ->
                                    navController.navigate(CreateProductRoute(productId = productId))
                                }
                            )
                        }

                        composable<ViewProductRoute> {
                            val productId = it.toRoute<ViewProductRoute>().productId
                            ViewProductScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToEdit = { id ->
                                    navController.navigate(CreateProductRoute(productId = id))
                                },
                                productId = productId
                            )
                        }

                        composable<CreateProductRoute> {
                            val productId = it.toRoute<CreateProductRoute>().productId
                            CreateProductScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToViewProduct = { id ->
                                    navController.popBackStack()
                                    navController.navigate(ViewProductRoute(productId = id))
                                },
                                productId = productId
                            )
                        }
                    }
                }
            }
        }
    }
}
