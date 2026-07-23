package ufsm.petsi.petservices.models

import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class PurchaseOrder @OptIn(ExperimentalTime::class) constructor(
    val idPurchaseOrder: String = UUID.randomUUID().toString(),
    val idClient: String,
    val date: String,
    val updatedAt: Instant = Clock.System.now(),
    val deleted: Boolean = false
)

