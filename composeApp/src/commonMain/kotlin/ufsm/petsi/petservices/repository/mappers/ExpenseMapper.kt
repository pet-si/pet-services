package ufsm.petsi.petservices.repository.mappers

import ufsm.petsi.petservices.database.ExpenseEntity
import ufsm.petsi.petservices.models.Expense
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun ExpenseEntity.toModel() : Expense {
    return Expense(
        idExpense = idExpense,
        idUser = idUser,
        name = name,
        amount = amount,
        date = date,
        category = category,
        updatedAt = mapEntityDate(updatedAt),
        deleted = deleted
    )
}