package ufsm.petsi.petservices.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object SignupRoute

@Serializable
data class HomeRoute(val userId : String)