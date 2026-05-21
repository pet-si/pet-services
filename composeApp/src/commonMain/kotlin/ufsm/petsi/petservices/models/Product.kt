package ufsm.petsi.petservices.models

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class Product @OptIn(ExperimentalTime::class) constructor(
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

