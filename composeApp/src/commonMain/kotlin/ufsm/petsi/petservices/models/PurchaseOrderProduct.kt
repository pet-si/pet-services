package ufsm.petsi.petservices.models

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class PurchaseOrderProduct(
    val idPurchaseOrder: String,
    val idProduct: String,
    val quantity: Int = 1,
    val updatedAt: Instant = Clock.System.now(),
    val deleted: Boolean = false
)

