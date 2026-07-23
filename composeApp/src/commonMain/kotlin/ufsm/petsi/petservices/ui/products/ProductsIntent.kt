package ufsm.petsi.petservices.ui.products

import ufsm.petsi.petservices.models.Product

sealed interface ProductsIntent {
    data class OnProductClick(val productId: String) : ProductsIntent
    object OnAddProductClick : ProductsIntent
}

data class ProductsState(
    val isLoading : Boolean = false,
    val errorMessage : String? = null,
    val products: List<Product> = emptyList()
)

sealed interface ProductsEffects {
    data class NavigateToProductDetails(val productId: String) : ProductsEffects
}