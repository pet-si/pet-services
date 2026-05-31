package ufsm.petsi.petservices.repository.mappers

import ufsm.petsi.petservices.database.MaterialEntity
import ufsm.petsi.petservices.models.Material
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun MaterialEntity.toModel(): Material {
    return Material(
        idMaterial = idMaterial,
        name = name,
        quantity = quantity!!.toInt(),
        costPrice = costPrice,
        minimumStock = minimumStock!!.toInt(),
        metric = metric,
        updatedAt = mapEntityDate(updatedAt),
        deleted = deleted
    )
}

