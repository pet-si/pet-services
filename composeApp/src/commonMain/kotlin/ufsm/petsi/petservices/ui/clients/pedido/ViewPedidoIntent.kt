package ufsm.petsi.petservices.ui.clients.pedido

import ufsm.petsi.petservices.models.Product
import ufsm.petsi.petservices.models.PurchaseOrderProduct

sealed interface ViewPedidoIntent {
    data class LoadPedido(val orderId: String, val clientName: String = "") : ViewPedidoIntent
}

data class ViewPedidoState(
    val orderId: String = "",
    val date: String = "",
    val clientName: String = "",
    val products: List<PurchaseOrderProduct> = emptyList(),
    val availableProducts: List<Product> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)