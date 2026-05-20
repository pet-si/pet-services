package ufsm.petsi.petservices.repository.interfaces

import kotlinx.coroutines.flow.Flow
import ufsm.petsi.petservices.models.User
import ufsm.petsi.petservices.repository.DataResult

interface IUserRepository {
    fun getUser(): Flow<User?>

    fun loginUser(email: String, password: String): DataResult<User>
    suspend fun insertUser(user: User) : DataResult<Boolean>
    suspend fun updateUser(user: User)
    suspend fun updatePassword(idUser: String, newPassword: String)
    suspend fun deleteUser(idUser: String)
}