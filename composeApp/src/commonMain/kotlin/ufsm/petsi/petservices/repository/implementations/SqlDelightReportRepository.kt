package ufsm.petsi.petservices.repository.implementations

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ufsm.petsi.petservices.database.AppDatabase
import ufsm.petsi.petservices.repository.interfaces.ReportInterface
import ufsm.petsi.petservices.models.Report
import ufsm.petsi.petservices.repository.mappers.toModel

class SqlDelightReportRepository(database: AppDatabase) : ReportInterface {
    private val selectQueries = database.selectQueries
    private val insertQueries = database.insertQueries
    private val updateQueries = database.updateQueries
    private val deleteQueries = database.deleteQueries

    override fun getReport(): Flow<Report?> {
        return selectQueries.selectAllReports()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity ->
                entity?.toModel()
            }
    }

    override fun getAllReports(): Flow<List<Report>> {
        return selectQueries.selectAllReports()
            .asFlow()
            .map { query ->
                query.executeAsList().map { entity ->
                    entity.toModel()
                }
            }
    }

    override suspend fun insertReport(report: Report) {
        insertQueries.insertReport(
            idReport = report.idReport,
            filePath = report.filePath,
            date = report.date,
            deleted = report.deleted
        )
    }

    override suspend fun updateReport(report: Report) {
        updateQueries.updateReport(
            filePath = report.filePath,
            date = report.date,
            idReport = report.idReport
        )
    }

    override suspend fun deleteReport(report: Report) {
        deleteQueries.softDeleteReport(report.idReport)
    }
}
