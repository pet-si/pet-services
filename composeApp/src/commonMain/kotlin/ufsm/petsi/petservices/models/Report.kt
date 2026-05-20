package ufsm.petsi.petservices.models

import java.util.UUID
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class Report @OptIn(ExperimentalTime::class) constructor(
    val idReport: String = UUID.randomUUID().toString(),
    val filePath: String,
    val date: String,
    val updatedAt: Instant = Clock.System.now(),
    val deleted: Boolean = false,
)