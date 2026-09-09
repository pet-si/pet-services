package ufsm.petsi.petservices.repository.mappers

import ufsm.petsi.petservices.database.PurchaseOrderProductsEntity
import ufsm.petsi.petservices.models.PurchaseOrderProduct
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun PurchaseOrderProductsEntity.toModel(): PurchaseOrderProduct {
    return PurchaseOrderProduct(
        idPurchaseOrder = idPurchaseOrder,
        idProduct = idProduct,
        quantity = quantity?.toInt() ?: 1,
        updatedAt = mapEntityDate(updatedAt),
        deleted = deleted
    )
}