package ufsm.petsi.petservices.repository.implementations

import ufsm.petsi.petservices.database.AppDatabase
import ufsm.petsi.petservices.database.SincronizationEntity
import ufsm.petsi.petservices.models.Sincronization
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.UNKNOWN_ERROR
import ufsm.petsi.petservices.repository.interfaces.ISincronizationRepository
import ufsm.petsi.petservices.repository.mappers.toModel
import ufsm.petsi.petservices.repository.notFoundError

class SincronizationRepository(database: AppDatabase) : ISincronizationRepository {
    private val selectQueries = database.selectQueries
    private val insertQueries = database.insertQueries

    override fun getLatestSincronization(): DataResult<Sincronization> {
        return try {
            val updateTimeStamp = selectQueries.selectLatestSincronization().executeAsOneOrNull()
            if (updateTimeStamp != null) {
                DataResult.Success(SincronizationEntity(updateTimeStamp).toModel())
            } else {
                DataResult.Error(notFoundError("Setting"))
            }
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }

    override suspend fun insertSincronization(): DataResult<Boolean> {
        return try {
            insertQueries.insertSincronization()
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }
}
