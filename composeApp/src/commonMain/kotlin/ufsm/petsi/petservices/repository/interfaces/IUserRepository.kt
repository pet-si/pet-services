package ufsm.petsi.petservices.repository.interfaces

import kotlinx.coroutines.flow.Flow
import ufsm.petsi.petservices.models.User
import ufsm.petsi.petservices.repository.DataResult

interface IUserRepository {
    fun loginUser(email: String, password: String): DataResult<User>
    suspend fun insertUser(user: User) : DataResult<Boolean>
    suspend fun updateUser(user: User) : DataResult<Boolean>
    suspend fun updatePassword(idUser: String, newPassword: String) : DataResult<Boolean>
    suspend fun deleteUser(idUser: String) : DataResult<Boolean>
}