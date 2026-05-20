package ufsm.petsi.petservices.repository.mappers

import ufsm.petsi.petservices.models.Report
import ufsm.petsi.petservices.database.ReportEntity
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun ReportEntity.toModel(): Report {
    return Report(
        idReport = idReport,
        filePath =  filePath,
        date = date,
        updatedAt = mapEntityDate(updatedAt),
        deleted = deleted
    )
}