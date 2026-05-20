package ufsm.petsi.petservices.models

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class Material @OptIn(ExperimentalTime::class) constructor(
    val idMaterial: String,
    val name: String,
    val quantity: Int = 0,
    val costPrice: Double,
    val minimumStock: Int = 0,
    val metric: String,
    val updatedAt: Instant = Clock.System.now(),
    val deleted: Boolean = false
)

