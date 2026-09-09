package ufsm.petsi.petservices.repository.interfaces

import kotlinx.coroutines.flow.Flow
import ufsm.petsi.petservices.models.PurchaseOrder
import ufsm.petsi.petservices.models.PurchaseOrderProduct
import ufsm.petsi.petservices.repository.DataResult

interface IPurchaseOrderRepository {
    fun getAllPurchaseOrders(): Flow<DataResult<List<PurchaseOrder>>>
    fun getPurchaseOrdersByClientId(idClient: String): Flow<DataResult<List<PurchaseOrder>>>
    fun getPurchaseOrderById(idPurchaseOrder: String): DataResult<PurchaseOrder>
    fun getAllPurchaseOrderProducts(): Flow<DataResult<List<PurchaseOrderProduct>>>
    fun getPurchaseOrderProductsByOrderId(idPurchaseOrder: String): Flow<DataResult<List<PurchaseOrderProduct>>>
    suspend fun insertPurchaseOrder(
        purchaseOrder: PurchaseOrder,
        products: List<PurchaseOrderProduct>
    ): DataResult<Boolean>
}