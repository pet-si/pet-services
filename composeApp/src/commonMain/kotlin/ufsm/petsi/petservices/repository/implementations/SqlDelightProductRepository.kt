package ufsm.petsi.petservices.repository.implementations

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import ufsm.petsi.petservices.database.AppDatabase
import ufsm.petsi.petservices.models.Product
import ufsm.petsi.petservices.repository.interfaces.ProductRepository
import kotlinx.coroutines.flow.map
import ufsm.petsi.petservices.repository.mappers.toModel

class SqlDelightProductRepository( database: AppDatabase) : ProductRepository {
    private val selectQueries = database.selectQueries
    private val insertQueries = database.insertQueries
    private val updateQueries = database.updateQueries
    private val deleteQueries = database.deleteQueries

    override fun getProductById(id: String): Flow<Product?> {
        return selectQueries.selectProductById(id)
            .asFlow()
            .mapToOneOrNull(context = Dispatchers.IO)
            .map{ entity ->
                entity?.toModel()
            }
    }

    override fun getAllProducts(): Flow<List<Product>> {
        return selectQueries.selectAllProducts()
            .asFlow()
            .map { query ->
                query.executeAsList().map { entity ->
                    entity.toModel()
                }
            }
    }

    override suspend fun insertProduct(product: Product) {
        insertQueries.insertProduct(
            idProduct = product.idProduct,
            name = product.name,
            quantity = product.quantity,
            costPrice = product.costPrice,
            salePrice = product.salePrice,
            minimumStock = product.minimumStock.toLong(),
            soldQuantity = product.soldQuantity.toLong()
        )
    }

    override suspend fun updateProduct(product: Product) {
        updateQueries.updateProduct(
            name = product.name,
            quantity = product.quantity,
            costPrice = product.costPrice,
            salePrice = product.salePrice,
            minimumStock = product.minimumStock.toLong(),
            soldQuantity = product.soldQuantity.toLong(),
            idProduct = product.idProduct,
        )
    }

    override suspend fun deleteProduct(id: String) {
        deleteQueries.softDeleteProduct(id)
    }
}