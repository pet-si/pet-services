package ufsm.petsi.petservices.models

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class ProductMaterial @OptIn(ExperimentalTime::class) constructor(
    val idProduct: String,
    val idMaterial: String,
    val quantity: Double,
    val updatedAt: Instant = Clock.System.now(),
    val deleted: Boolean = false
)

