package ufsm.petsi.petservices.repository.interfaces

import kotlinx.coroutines.flow.Flow
import ufsm.petsi.petservices.models.Sincronization
import ufsm.petsi.petservices.repository.DataResult

interface ISincronizationRepository {
    fun getLatestSincronization(): DataResult<Sincronization>
    suspend fun insertSincronization(): DataResult<Boolean>
}

