package ufsm.petsi.petservices.repository.interfaces

import kotlinx.coroutines.flow.Flow
import ufsm.petsi.petservices.models.Product
import ufsm.petsi.petservices.models.Report
import ufsm.petsi.petservices.repository.DataResult

interface IReportRepository {
    fun getReportById(id: String, idUser: String) : DataResult<Report>
    fun getAllReports(idUser: String) : Flow<DataResult<List<Report>>>
    suspend fun insertReport(report: Report) : DataResult<Boolean>
    suspend fun updateReport(report: Report) : DataResult<Boolean>
    suspend fun deleteReport(report: Report) : DataResult<Boolean>
}