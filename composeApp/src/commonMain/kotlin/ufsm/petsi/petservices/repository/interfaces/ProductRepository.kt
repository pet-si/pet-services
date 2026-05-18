package ufsm.petsi.petservices.repository.interfaces

import kotlinx.coroutines.flow.Flow
import ufsm.petsi.petservices.models.Product

interface ProductRepository {
    fun getProductById(id: String): Flow<Product?>
    fun getAllProducts(): Flow<List<Product>>
    suspend fun insertProduct(product: Product)
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(id: String)
}