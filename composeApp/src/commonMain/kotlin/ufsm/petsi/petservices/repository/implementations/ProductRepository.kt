package ufsm.petsi.petservices.repository.implementations

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ufsm.petsi.petservices.database.AppDatabase
import ufsm.petsi.petservices.models.Product
import ufsm.petsi.petservices.models.ProductMaterial
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.UNKNOWN_ERROR
import ufsm.petsi.petservices.repository.interfaces.IProductRepository
import ufsm.petsi.petservices.repository.mappers.mapEntityDate
import ufsm.petsi.petservices.repository.notFoundError
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ProductRepository(private val database: AppDatabase) : IProductRepository {
    private val selectQueries = database.selectQueries
    private val insertQueries = database.insertQueries
    private val updateQueries = database.updateQueries
    private val deleteQueries = database.deleteQueries

    override fun getProductById(id: String, idUser: String): DataResult<Product> {
        return try {
            val rows = selectQueries.selectProductWithMaterialsById(idUser, id, idUser).executeAsList()
            if (rows.isNotEmpty()) {
                val first = rows.first()
                val materials = rows.mapNotNull { row ->
                    row.pm_idMaterial?.let {
                        ProductMaterial(
                            idProduct = first.idProduct,
                            idMaterial = it,
                            idUser = row.pm_idUser!!,
                            quantity = row.pm_quantity!!,
                            updatedAt = mapEntityDate(row.pm_updatedAt!!),
                            deleted = row.pm_deleted ?: false
                        )
                    }
                }
                DataResult.Success(
                    Product(
                        idProduct = first.idProduct,
                        idUser = first.idUser,
                        name = first.name,
                        quantity = first.quantity,
                        costPrice = first.costPrice,
                        salePrice = first.salePrice,
                        minimumStock = first.minimumStock!!.toInt(),
                        soldQuantity = first.soldQuantity!!.toInt(),
                        materials = materials,
                        updatedAt = mapEntityDate(first.updatedAt),
                        deleted = first.deleted
                    )
                )
            } else {
                DataResult.Error(notFoundError("Produto"))
            }
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }

    override fun getAllProducts(idUser: String): Flow<DataResult<List<Product>>> {
        return selectQueries.selectAllProductsWithMaterials(idUser, idUser)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows ->
                val products = rows.groupBy { it.idProduct }.map { (productId, group) ->
                    val first = group.first()
                    val materials = group.mapNotNull { row ->
                        row.pm_idMaterial?.let {
                            ProductMaterial(
                                idProduct = productId,
                                idMaterial = it,
                                idUser = row.pm_idUser!!,
                                quantity = row.pm_quantity!!,
                                updatedAt = mapEntityDate(row.pm_updatedAt!!),
                                deleted = row.pm_deleted ?: false
                            )
                        }
                    }
                    Product(
                        idProduct = first.idProduct,
                        idUser = first.idUser,
                        name = first.name,
                        quantity = first.quantity,
                        costPrice = first.costPrice,
                        salePrice = first.salePrice,
                        minimumStock = first.minimumStock!!.toInt(),
                        soldQuantity = first.soldQuantity!!.toInt(),
                        materials = materials,
                        updatedAt = mapEntityDate(first.updatedAt),
                        deleted = first.deleted
                    )
                }
                DataResult.Success(products)
            }
            .catch { e -> emit(DataResult.Error(e.message ?: UNKNOWN_ERROR)) }
    }

    override suspend fun insertProduct(product: Product) : DataResult<Boolean> {
        return  try {
            database.transaction {
                insertQueries.insertProduct(
                    idProduct = product.idProduct,
                    idUser = product.idUser,
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
                        idUser = product.idUser,
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
                    idUser = product.idUser,
                )

                deleteQueries.softDeleteAllProductMaterials(product.idProduct, product.idUser)

                product.materials.forEach { pm ->
                    insertQueries.insertProductMaterial(
                        idProduct = product.idProduct,
                        idMaterial = pm.idMaterial,
                        idUser = product.idUser,
                        quantity = pm.quantity
                    )
                }
            }
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }

    override suspend fun deleteProduct(id: String, idUser: String) : DataResult<Boolean> {
        return try {
            deleteQueries.softDeleteProduct(id, idUser)
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }
}
