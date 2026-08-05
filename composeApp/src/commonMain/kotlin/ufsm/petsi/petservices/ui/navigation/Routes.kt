package ufsm.petsi.petservices.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

@Serializable
data object SignupRoute

@Serializable
data class MainGraph(val userId : String)

@Serializable
data object HomeRoute

@Serializable
data object ProductRoute

@Serializable
data class CreateProductRoute(val productId : String? = null)