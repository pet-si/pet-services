package ufsm.petsi.petservices.ui.products

import ufsm.petsi.petservices.models.Product

sealed interface ProductsIntent {
    data class OnProductClick(val productId: String) : ProductsIntent
    data class OnProductEdit(val productId: String) : ProductsIntent
    data class OnProductDuplicate(val productId: String) : ProductsIntent
    data object OnAddProductClick : ProductsIntent
    data class OnProductDelete(val productId: String) : ProductsIntent
    data object ConfirmDeleteProduct : ProductsIntent
    data object DismissDeleteDialog : ProductsIntent
}

data class ProductsState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val products: List<Product> = emptyList(),
    val showDeleteDialog: Boolean = false,
    val productToDelete: Product? = null,
)

sealed interface ProductsEffects {
    data class NavigateToProductDetails(val productId: String) : ProductsEffects
    data class NavigateToProductEdit(val productId: String) : ProductsEffects
    data object NavigateToProductCreation : ProductsEffects
}
