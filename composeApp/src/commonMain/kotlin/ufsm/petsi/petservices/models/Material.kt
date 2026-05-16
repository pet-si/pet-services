package ufsm.petsi.petservices.models

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Material(
    val idMaterial: String,
    val name: String,
    val quantity: Int = 0,
    val costPrice: Double,
    val minimumStock: Int = 0,
    val metric: String,
    val updatedAt: Instant = Clock.System.now(),
    val deleted: Boolean = false
)

