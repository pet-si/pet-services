package ufsm.petsi.petservices.models

import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class Expense @OptIn(ExperimentalTime::class) constructor(
    val idExpense: String = UUID.randomUUID().toString(),
    val name: String,
    val amount: Double,
    val date: String,
    val category: String,
    val updatedAt: Instant = Clock.System.now(),
    val deleted: Boolean = false
)

