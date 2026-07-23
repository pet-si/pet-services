package ufsm.petsi.petservices.repository.interfaces

import kotlinx.coroutines.flow.Flow
import ufsm.petsi.petservices.models.Product
import ufsm.petsi.petservices.repository.DataResult

interface IProductRepository {
    fun getProductById(id: String, idUser: String): DataResult<Product>
    fun getAllProducts(idUser: String): Flow<DataResult<List<Product>>>
    suspend fun insertProduct(product: Product) : DataResult<Boolean>
    suspend fun updateProduct(product: Product) : DataResult<Boolean>
    suspend fun deleteProduct(id: String, idUser: String) : DataResult<Boolean>
}