package ufsm.petsi.petservices.repository.implementations

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ufsm.petsi.petservices.database.AppDatabase
import ufsm.petsi.petservices.database.ClientEntity
import ufsm.petsi.petservices.models.Client
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.UNKNOWN_ERROR
import ufsm.petsi.petservices.repository.interfaces.IClientRepository
import ufsm.petsi.petservices.repository.mappers.toModel
import ufsm.petsi.petservices.repository.notFoundError
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ClientRepository(private val database: AppDatabase) : IClientRepository {
    private val selectQueries = database.selectQueries
    private val insertQueries = database.insertQueries
    private val updateQueries = database.updateQueries
    private val deleteQueries = database.deleteQueries

    override fun getClientById(id: String): DataResult<Client> {
        return try {
            val clientEntity = selectQueries.selectClientById(id).executeAsOneOrNull()
            if (clientEntity != null) {
                DataResult.Success(clientEntity.toModel())
            } else {
                DataResult.Error(notFoundError("Cliente"))
            }
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }

    override fun getAllClients(): Flow<DataResult<List<Client>>> {
        return selectQueries.selectAllClients()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map<List<ClientEntity>, DataResult<List<Client>>> { list ->
                DataResult.Success(list.map { it.toModel() })
            }
            .catch { e -> emit(DataResult.Error(e.message ?: UNKNOWN_ERROR)) }
    }

    override suspend fun insertClient(client: Client) : DataResult<Boolean> {
        return  try {
            insertQueries.insertClient(
                idClient = client.idClient,
                name = client.name,
                cpf = client.cpf,
                email = client.email,
                phoneNumber = client.phoneNumber,
                points = client.points.toLong()
            )
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }

    override suspend fun updateClient(client: Client) : DataResult<Boolean> {
        return try {
            updateQueries.updateClient(
                name = client.name,
                cpf = client.cpf,
                email = client.email,
                phoneNumber = client.phoneNumber,
                points = client.points.toLong(),
                idClient = client.idClient
            )
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }

    override suspend fun deleteClient(id: String) : DataResult<Boolean> {
        return try {
            deleteQueries.softDeleteClient(id)
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }
}