package ufsm.petsi.petservices.repository.implementations

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ufsm.petsi.petservices.database.AppDatabase
import ufsm.petsi.petservices.database.ProductEntity
import ufsm.petsi.petservices.models.Product
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.UNKNOWN_ERROR
import ufsm.petsi.petservices.repository.interfaces.IProductRepository
import ufsm.petsi.petservices.repository.mappers.toModel
import ufsm.petsi.petservices.repository.notFoundError
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ProductRepository(private val database: AppDatabase) : IProductRepository {
    private val selectQueries = database.selectQueries
    private val insertQueries = database.insertQueries
    private val updateQueries = database.updateQueries
    private val deleteQueries = database.deleteQueries

    override fun getProductById(id: String): DataResult<Product> {
        return try {
            val productEntity = selectQueries.selectProductById(id).executeAsOneOrNull()
            if (productEntity != null) {
                val materials = selectQueries.selectMaterialsByProductId(id)
                    .executeAsList()
                    .map { it.toModel() }
                DataResult.Success(productEntity.toModel().copy(materials = materials))
            } else {
                DataResult.Error(notFoundError("Produto"))
            }
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }

    override fun getAllProducts(): Flow<DataResult<List<Product>>> {
        return selectQueries.selectAllProducts()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map<List<ProductEntity>, DataResult<List<Product>>> { list ->
                DataResult.Success(list.map { entity ->
                    val materials = selectQueries.selectMaterialsByProductId(entity.idProduct)
                        .executeAsList()
                        .map { it.toModel() }
                    entity.toModel().copy(materials = materials)
                })
            }
            .catch { e -> emit(DataResult.Error(e.message ?: UNKNOWN_ERROR)) }
    }

    override suspend fun insertProduct(product: Product) : DataResult<Boolean> {
        return  try {
            database.transaction {
                insertQueries.insertProduct(
                    idProduct = product.idProduct,
                    name = product.name,
                    quantity = product.quantity,
                    costPrice = product.costPrice,
                    salePrice = product.salePrice,
                    minimumStock = product.minimumStock.toLong(),
                    soldQuantity = product.soldQuantity.toLong()
                )
                product.materials.forEach { pm ->
                    insertQueries.insertProductMaterial(
                        idProduct = product.idProduct,
                        idMaterial = pm.idMaterial,
                        quantity = pm.quantity
                    )
                }
            }
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }

    override suspend fun updateProduct(product: Product) : DataResult<Boolean> {
        return try {
            database.transaction {
                updateQueries.updateProduct(
                    name = product.name,
                    quantity = product.quantity,
                    costPrice = product.costPrice,
                    salePrice = product.salePrice,
                    minimumStock = product.minimumStock.toLong(),
                    soldQuantity = product.soldQuantity.toLong(),
                    idProduct = product.idProduct,
                )

                deleteQueries.softDeleteAllProductMaterials(product.idProduct)

                product.materials.forEach { pm ->
                    insertQueries.insertProductMaterial(
                        idProduct = product.idProduct,
                        idMaterial = pm.idMaterial,
                        quantity = pm.quantity
                    )
                }
            }
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }

    override suspend fun deleteProduct(id: String) : DataResult<Boolean> {
        return try {
            deleteQueries.softDeleteProduct(id)
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }
}