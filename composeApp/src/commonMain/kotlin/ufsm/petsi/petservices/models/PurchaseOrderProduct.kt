package ufsm.petsi.petservices.models

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class PurchaseOrderProduct @OptIn(ExperimentalTime::class) constructor(
    val idPurchaseOrder: String,
    val idProduct: String,
    val quantity: Int = 1,
    val updatedAt: Instant = Clock.System.now(),
    val deleted: Boolean = false
)

