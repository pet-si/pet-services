package ufsm.petsi.petservices.repository.mappers

import ufsm.petsi.petservices.database.SincronizationEntity
import ufsm.petsi.petservices.models.Sincronization
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun SincronizationEntity.toModel(): Sincronization {
    return Sincronization(
        updatesTimeStamp = mapEntityDate(updateTimeStamp)
    )
}
