package ufsm.petsi.petservices.repository.implementations

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ufsm.petsi.petservices.database.AppDatabase
import ufsm.petsi.petservices.database.PurchaseOrderEntity
import ufsm.petsi.petservices.database.PurchaseOrderProductsEntity
import ufsm.petsi.petservices.models.PurchaseOrder
import ufsm.petsi.petservices.models.PurchaseOrderProduct
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.UNKNOWN_ERROR
import ufsm.petsi.petservices.repository.interfaces.IPurchaseOrderRepository
import ufsm.petsi.petservices.repository.mappers.toModel
import ufsm.petsi.petservices.repository.notFoundError
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class PurchaseOrderRepository(private val database: AppDatabase) : IPurchaseOrderRepository {
    private val selectQueries = database.selectQueries
    private val insertQueries = database.insertQueries

    override fun getAllPurchaseOrders(): Flow<DataResult<List<PurchaseOrder>>> {
        return selectQueries.selectAllPurchaseOrders()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map<List<PurchaseOrderEntity>, DataResult<List<PurchaseOrder>>> { list ->
                DataResult.Success(list.map { it.toModel() })
            }
            .catch { e -> emit(DataResult.Error(e.message ?: UNKNOWN_ERROR)) }
    }

    override fun getPurchaseOrdersByClientId(idClient: String): Flow<DataResult<List<PurchaseOrder>>> {
        return selectQueries.selectPurchaseOrdersByClientId(idClient)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map<List<PurchaseOrderEntity>, DataResult<List<PurchaseOrder>>> { list ->
                DataResult.Success(list.map { it.toModel() })
            }
            .catch { e -> emit(DataResult.Error(e.message ?: UNKNOWN_ERROR)) }
    }

    override fun getPurchaseOrderById(idPurchaseOrder: String): DataResult<PurchaseOrder> {
        return try {
            val entity = selectQueries.selectPurchaseOrderById(idPurchaseOrder).executeAsOneOrNull()
            if (entity != null) {
                DataResult.Success(entity.toModel())
            } else {
                DataResult.Error(notFoundError("Pedido"))
            }
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }

    override fun getAllPurchaseOrderProducts(): Flow<DataResult<List<PurchaseOrderProduct>>> {
        return selectQueries.selectAllPurchaseOrderProducts()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map<List<PurchaseOrderProductsEntity>, DataResult<List<PurchaseOrderProduct>>> { list ->
                DataResult.Success(list.map { it.toModel() })
            }
            .catch { e -> emit(DataResult.Error(e.message ?: UNKNOWN_ERROR)) }
    }

    override fun getPurchaseOrderProductsByOrderId(idPurchaseOrder: String): Flow<DataResult<List<PurchaseOrderProduct>>> {
        return selectQueries.selectPurchaseOrderProductsByOrderId(idPurchaseOrder)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map<List<PurchaseOrderProductsEntity>, DataResult<List<PurchaseOrderProduct>>> { list ->
                DataResult.Success(list.map { it.toModel() })
            }
            .catch { e -> emit(DataResult.Error(e.message ?: UNKNOWN_ERROR)) }
    }

    override suspend fun insertPurchaseOrder(
        purchaseOrder: PurchaseOrder,
        products: List<PurchaseOrderProduct>
    ): DataResult<Boolean> {
        return try {
            database.transaction {
                insertQueries.insertPurchaseOrder(
                    idPurchaseOrder = purchaseOrder.idPurchaseOrder,
                    idClient = purchaseOrder.idClient,
                    date = purchaseOrder.date
                )
                products.forEach { product ->
                    insertQueries.insertPurchaseOrderProduct(
                        idPurchaseOrder = purchaseOrder.idPurchaseOrder,
                        idProduct = product.idProduct,
                        quantity = product.quantity.toLong()
                    )
                }
            }
            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: UNKNOWN_ERROR)
        }
    }
}