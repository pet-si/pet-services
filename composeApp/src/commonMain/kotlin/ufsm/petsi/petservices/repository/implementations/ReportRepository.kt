package ufsm.petsi.petservices.repository.implementations

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ufsm.petsi.petservices.database.AppDatabase
import ufsm.petsi.petservices.database.ProductEntity
import ufsm.petsi.petservices.database.ReportEntity
import ufsm.petsi.petservices.models.Product
import ufsm.petsi.petservices.repository.interfaces.IReportRepository
import ufsm.petsi.petservices.models.Report
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.UNKNOWN_ERROR
import ufsm.petsi.petservices.repository.mappers.toModel
import ufsm.petsi.petservices.repository.notFoundError
import kotlin.collections.map

class ReportRepository(database: AppDatabase) : IReportRepository {
    private val selectQueries = database.selectQueries
    private val insertQueries = database.insertQueries
    private val updateQueries = database.updateQueries
    private val deleteQueries = database.deleteQueries

    override fun getReportById(id: String, idUser: String): DataResult<Report> {
        return try {
            val report = selectQueries.selectReportById(id, idUser).executeAsOneOrNull()?.toModel()
            if (report != null) {
                DataResult.Success(report)
            } else {
                DataResult.Error(notFoundError("Relatório"))
            }
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }

    override fun getAllReports(idUser: String): Flow<DataResult<List<Report>>> {
        return selectQueries.selectAllReports(idUser)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map<List<ReportEntity>, DataResult<List<Report>>> { list -> DataResult.Success(list.map { it.toModel() }) }
            .catch { e -> emit(DataResult.Error(e.message ?: UNKNOWN_ERROR)) }
    }

    override suspend fun insertReport(report: Report) : DataResult<Boolean> {
        return try {
            insertQueries.insertReport(
                idReport = report.idReport,
                idUser = report.idUser,
                filePath = report.filePath,
                date = report.date
            )
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }

    override suspend fun updateReport(report: Report) : DataResult<Boolean> {
        return try {
            updateQueries.updateReport(
                filePath = report.filePath,
                date = report.date,
                idReport = report.idReport,
                idUser = report.idUser
            )
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }

    override suspend fun deleteReport(report: Report) : DataResult<Boolean> {
        return try {
            deleteQueries.softDeleteReport(report.idReport, report.idUser)
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }
}
