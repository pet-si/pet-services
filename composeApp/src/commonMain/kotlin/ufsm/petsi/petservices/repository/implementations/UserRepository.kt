package ufsm.petsi.petservices.repository.implementations

import ufsm.petsi.petservices.database.AppDatabase
import ufsm.petsi.petservices.models.User
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.interfaces.IUserRepository
import ufsm.petsi.petservices.repository.mappers.toModel
import ufsm.petsi.petservices.repository.UNKNOWN_ERROR
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class UserRepository(database: AppDatabase) : IUserRepository {
    private val selectQueries = database.selectQueries
    private val insertQueries = database.insertQueries
    private val updateQueries = database.updateQueries
    private val deleteQueries = database.deleteQueries

    override fun loginUser(email: String, password: String): DataResult<User> {
        return try {
            val user = selectQueries.selectUserLogin(email, password).executeAsOneOrNull()?.toModel()
            if (user != null) {
                return DataResult.Success(user)
            } else {
                DataResult.Error("Email ou Senha incorretos.")
            }
        } catch (e : Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR, e)
        }
    }

    override suspend fun insertUser(user: User): DataResult<Boolean> {
        return try {
            if (user.password.isNullOrEmpty()) return DataResult.Error("Por favor insira uma senha válida.")
            insertQueries.insertUser(
                idUser = user.idUser,
                name = user.name,
                email = user.email,
                companyName = user.companyName,
                password = user.password
            )
            DataResult.Success(true)
        } catch (e : Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR, e)
        }
    }

    override suspend fun updateUser(user: User) : DataResult<Boolean> {
        return try {
            updateQueries.updateUser(
                name = user.name,
                email = user.email,
                companyName = user.companyName,
                idUser = user.idUser
            )
            DataResult.Success(true)
        } catch (e : Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR, e)
        }
    }

    override suspend fun updatePassword(idUser: String, newPassword: String) : DataResult<Boolean> {
        return try {
            updateQueries.updatePassword(
                password = newPassword,
                idUser = idUser
            )
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR, e)
        }
    }

    override suspend fun deleteUser(idUser: String) : DataResult<Boolean> {
        return try {
            deleteQueries.softDeleteUser(idUser)
            DataResult.Success(true)
        } catch(e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR, e)
        }
    }
}