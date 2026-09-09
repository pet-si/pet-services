package ufsm.petsi.petservices.repository.interfaces

import kotlinx.coroutines.flow.Flow
import ufsm.petsi.petservices.models.Client
import ufsm.petsi.petservices.repository.DataResult

interface IClientRepository {
    fun getClientById(id: String): DataResult<Client>
    fun getAllClients(): Flow<DataResult<List<Client>>>
    suspend fun insertClient(client: Client) : DataResult<Boolean>
    suspend fun updateClient(client: Client) : DataResult<Boolean>
    suspend fun deleteClient(id: String) : DataResult<Boolean>
}