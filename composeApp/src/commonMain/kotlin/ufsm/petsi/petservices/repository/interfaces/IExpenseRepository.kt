package ufsm.petsi.petservices.repository.interfaces

import kotlinx.coroutines.flow.Flow
import ufsm.petsi.petservices.models.Expense
import ufsm.petsi.petservices.repository.DataResult

interface IExpenseRepository {
    fun getExpenseById(expenseId: String, idUser: String): Flow<DataResult<Expense>>
    fun getAllExpenses(idUser: String): Flow<List<Expense>>
    suspend fun insertExpense(expense: Expense): DataResult<Boolean>
    suspend fun updateExpense(expense: Expense): DataResult<Boolean>
    suspend fun deleteExpense(id: String, idUser: String): DataResult<Boolean>
}