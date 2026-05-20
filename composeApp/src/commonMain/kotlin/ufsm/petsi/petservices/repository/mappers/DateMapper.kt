package ufsm.petsi.petservices.repository.mappers

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun mapEntityDate(date : String): Instant {
    val isoString = date.replace(" ", "T").let {
        if (!it.endsWith("Z")) "${it}Z" else it
    }

    return Instant.parse(isoString)
}
