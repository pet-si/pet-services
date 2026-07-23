package ufsm.petsi.petservices.repository.mappers

import ufsm.petsi.petservices.models.PurchaseOrder
import ufsm.petsi.petservices.database.PurchaseOrderEntity
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun PurchaseOrderEntity.toModel(): PurchaseOrder {
    return PurchaseOrder(
        idPurchaseOrder = idPurchaseOrder,
        idUser = idUser,
        idClient = idClient,
        date = date,
        updatedAt = mapEntityDate(updatedAt),
        deleted = deleted
    )
}
