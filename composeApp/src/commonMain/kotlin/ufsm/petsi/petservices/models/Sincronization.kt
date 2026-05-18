package ufsm.petsi.petservices.models

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Sincronization(
    val updatesTimeStamp: Instant = Clock.System.now()
)

