package ufsm.petsi.petservices.models

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ProductMaterial(
    val idProduct: String,
    val idMaterial: String,
    val quantity: Double,
    val updatedAt: Instant = Clock.System.now(),
    val deleted: Boolean = false
)

