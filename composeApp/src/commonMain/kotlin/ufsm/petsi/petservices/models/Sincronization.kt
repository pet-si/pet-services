package ufsm.petsi.petservices.models

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class Sincronization @OptIn(ExperimentalTime::class) constructor(
    val updatesTimeStamp: Instant = Clock.System.now()
)

