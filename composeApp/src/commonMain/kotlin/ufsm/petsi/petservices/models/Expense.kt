package ufsm.petsi.petservices.models

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Expense(
    val idExpense: String,
    val name: String,
    val amount: Double,
    val date: String,
    val category: String,
    val updatedAt: Instant = Clock.System.now(),
    val deleted: Boolean = false
)

