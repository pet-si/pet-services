package ufsm.petsi.petservices.models

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID

data class Report (
    val idReport: String = UUID.randomUUID().toString(),
    val filePath: String,
    val date: String,
    val updatedAt: Instant = Clock.System.now(),
    val deleted: Boolean = false,
)