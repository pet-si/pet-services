package ufsm.petsi.petservices.ui.clients

import ufsm.petsi.petservices.models.Client
import ufsm.petsi.petservices.models.Product
import ufsm.petsi.petservices.models.PurchaseOrder
import ufsm.petsi.petservices.models.PurchaseOrderProduct

sealed interface ClientsIntent {
    data class OnClientClick(val clientId: String) : ClientsIntent
    data class OnClientView(val clientId: String) : ClientsIntent
    data class OnClientEdit(val clientId: String) : ClientsIntent
    data object OnAddClientClick : ClientsIntent
    data class OnClientDelete(val clientId: String) : ClientsIntent
    data object ConfirmDeleteClient : ClientsIntent
    data object DismissDeleteDialog : ClientsIntent
}

data class ClientsState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val clients: List<Client> = emptyList(),
    val pedidosByClient: Map<String, List<PurchaseOrder>> = emptyMap(),
    val purchaseOrderProductsByOrder: Map<String, List<PurchaseOrderProduct>> = emptyMap(),
    val productsById: Map<String, Product> = emptyMap(),
    val expandedClientId: String? = null,
    val showDeleteDialog: Boolean = false,
    val clientToDelete: Client? = null,
)

sealed interface ClientsEffects {
    data class NavigateToClientDetails(val clientId: String) : ClientsEffects
    data class NavigateToClientEdit(val clientId: String) : ClientsEffects
    data object NavigateToClientCreation : ClientsEffects
}