package ufsm.petsi.petservices.repository.interfaces

import kotlinx.coroutines.flow.Flow
import ufsm.petsi.petservices.models.Report

interface ReportRepository {
    fun getReport() : Flow<Report?>
    fun getAllReports(): Flow<List<Report>>
    suspend fun insertReport(report: Report)
    suspend fun updateReport(report: Report)
    suspend fun deleteReport(report: Report)
}