package ufsm.petsi.petservices.repository.mappers

import ufsm.petsi.petservices.database.ProductMaterialsEntity
import ufsm.petsi.petservices.models.ProductMaterial
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun ProductMaterialsEntity.toModel(): ProductMaterial {
    return ProductMaterial(
        idProduct = idProduct,
        idMaterial = idMaterial,
        quantity = quantity,
        updatedAt = mapEntityDate(updatedAt),
        deleted = deleted
    )
}

