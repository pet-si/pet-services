package ufsm.petsi.petservices.repository.mappers

import kotlinx.datetime.Instant
import ufsm.petsi.petservices.models.Product
import ufsm.petsi.petservices.database.ProductEntity

fun ProductEntity.toModel(): Product {
    return Product(
        idProduct = idProduct,
        name = name,
        quantity = quantity,
        costPrice = costPrice,
        salePrice = salePrice,
        minimumStock = minimumStock!!.toInt(),
        soldQuantity = soldQuantity!!.toInt(),
        updatedAt = Instant.parse(updatedAt),
        deleted = deleted
    )
}