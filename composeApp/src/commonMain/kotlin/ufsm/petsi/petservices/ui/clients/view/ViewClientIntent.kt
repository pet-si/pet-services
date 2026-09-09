package ufsm.petsi.petservices.ui.clients.view

import ufsm.petsi.petservices.models.PurchaseOrder

sealed interface ViewClientIntent {
    data class LoadClient(val clientId: String) : ViewClientIntent
    data object DeleteClient : ViewClientIntent
    data object ConfirmDeleteClient : ViewClientIntent
    data object DismissDeleteDialog : ViewClientIntent
}

data class ViewClientState(
    val clientId: String = "",
    val name: String = "",
    val cpf: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val points: String = "",
    val pedidos: List<PurchaseOrder> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showDeleteDialog: Boolean = false,
)

sealed interface ViewClientEffects {
    data object NavigateBack : ViewClientEffects
    data class NavigateToEdit(val clientId: String) : ViewClientEffects
    data class ShowMessage(val message: String) : ViewClientEffects
}