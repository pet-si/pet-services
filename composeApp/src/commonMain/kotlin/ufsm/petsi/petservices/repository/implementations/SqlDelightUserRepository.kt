package ufsm.petsi.petservices.repository.implementations

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ufsm.petsi.petservices.database.AppDatabase
import ufsm.petsi.petservices.models.User
import ufsm.petsi.petservices.repository.interfaces.UserRepository
import ufsm.petsi.petservices.repository.mappers.toModel

class SqlDelightUserRepository( database: AppDatabase) : UserRepository {

    private val selectQueries = database.selectQueries
    private val insertQueries = database.insertQueries
    private val updateQueries = database.updateQueries
    private val deleteQueries = database.deleteQueries

    override fun getUser(): Flow<User?> {
        return selectQueries.selectAllUsers()
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { entity ->
                entity?.toModel()
            }
    }

    override suspend fun insertUser(user: User) {
        insertQueries.insertUser(
            idUser = user.idUser,
            name = user.name,
            email = user.email,
            companyName = user.companyName,
            password = user.password,
            deleted = user.deleted
        )
    }

    override suspend fun updateUser(user: User) {
        updateQueries.updateUser(
            name = user.name,
            email = user.email,
            companyName = user.companyName,
            password = user.password,
            idUser = user.idUser
        )
    }

    override suspend fun deleteUser(id: String) {
        deleteQueries.softDeleteUser(id)
    }
}