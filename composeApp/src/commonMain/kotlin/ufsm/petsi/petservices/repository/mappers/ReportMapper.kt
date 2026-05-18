package ufsm.petsi.petservices.repository.mappers

import ufsm.petsi.petservices.models.Report
import kotlinx.datetime.Instant
import ufsm.petsi.petservices.database.ReportEntity

fun ReportEntity.toModel(): Report {
    return Report(
        idReport = idReport,
        filePath =  filePath,
        date = date,
        updatedAt = Instant.parse(updatedAt),
        deleted = false
    )
}