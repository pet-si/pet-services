package ufsm.petsi.petservices.ui.clients.pedido

import ufsm.petsi.petservices.models.Product
import ufsm.petsi.petservices.models.PurchaseOrderProduct

sealed interface CreatePedidoIntent {
    data class DateChanged(val date: String) : CreatePedidoIntent
    data object OpenProductPicker : CreatePedidoIntent
    data object CloseProductPicker : CreatePedidoIntent
    data class ProductSearchChanged(val query: String) : CreatePedidoIntent
    data class SelectProduct(val product: Product) : CreatePedidoIntent
    data class QuantityChanged(val quantity: String) : CreatePedidoIntent
    data object ConfirmProduct : CreatePedidoIntent
    data class RemoveProduct(val idProduct: String) : CreatePedidoIntent
    data object Submit : CreatePedidoIntent
    data object Cancel : CreatePedidoIntent
}

data class CreatePedidoState(
    val clientId: String = "",
    val date: String = "",
    val products: List<PurchaseOrderProduct> = emptyList(),
    val availableProducts: List<Product> = emptyList(),
    val openProductPicker: Boolean = false,
    val selectedProduct: Product? = null,
    val productQuantity: String = "",
    val productSearchQuery: String = "",
    val isLoading: Boolean = false,
    val isProductsLoading: Boolean = false,
    val productsErrorMessage: String? = null,
    val dateError: String? = null,
    val quantityError: String? = null,
    val productsError: String? = null,
)

sealed interface CreatePedidoEffects {
    data object NavigateBack : CreatePedidoEffects
    data class ShowMessage(val message: String) : CreatePedidoEffects
}