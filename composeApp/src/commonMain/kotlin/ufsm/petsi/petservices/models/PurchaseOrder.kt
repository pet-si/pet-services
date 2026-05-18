package ufsm.petsi.petservices.models

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class PurchaseOrder(
    val idPurchaseOrder: String,
    val idClient: String,
    val date: String,
    val updatedAt: Instant = Clock.System.now(),
    val deleted: Boolean = false
)

