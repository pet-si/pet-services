package ufsm.petsi.petservices.repository.interfaces

import kotlinx.coroutines.flow.Flow
import sun.security.util.Password
import ufsm.petsi.petservices.models.User

interface UserRepository {
    fun getUser(): Flow<User?>
    suspend fun insertUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun updatePassword(idUser: String, newPassword: String)
    suspend fun deleteUser(idUser: String)
}