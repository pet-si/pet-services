package ufsm.petsi.petservices.models

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val idProduct: String,
    val name: String,
    val quantity: Long? = 0,
    val costPrice: Double,
    val salePrice: Double,
    val minimumStock: Int = 0,
    val soldQuantity: Int = 0,
    val updatedAt: Instant = Clock.System.now(),
    val deleted: Boolean = false
)

