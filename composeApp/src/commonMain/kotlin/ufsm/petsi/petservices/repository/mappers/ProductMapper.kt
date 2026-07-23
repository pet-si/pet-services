package ufsm.petsi.petservices.repository.mappers

import ufsm.petsi.petservices.models.Product
import ufsm.petsi.petservices.database.ProductEntity
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun ProductEntity.toModel(): Product {
    return Product(
        idProduct = idProduct,
        idUser = idUser,
        name = name,
        quantity = quantity,
        costPrice = costPrice,
        salePrice = salePrice,
        minimumStock = minimumStock!!.toInt(),
        soldQuantity = soldQuantity!!.toInt(),
        updatedAt = mapEntityDate(updatedAt),
        deleted = deleted
    )
}