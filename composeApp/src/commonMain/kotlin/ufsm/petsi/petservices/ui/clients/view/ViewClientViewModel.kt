package ufsm.petsi.petservices.ui.clients.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.implementations.ClientRepository
import ufsm.petsi.petservices.repository.implementations.PurchaseOrderRepository

class ViewClientViewModel(
    private val clientRepository: ClientRepository,
    private val purchaseOrderRepository: PurchaseOrderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ViewClientState())
    val state = _state.asStateFlow()

    private val _effects = MutableStateFlow<ViewClientEffects?>(null)

    fun handleIntent(intent: ViewClientIntent) {
        when (intent) {
            is ViewClientIntent.LoadClient -> loadClient(intent.clientId)
            ViewClientIntent.DeleteClient -> _state.update { it.copy(showDeleteDialog = true) }
            ViewClientIntent.ConfirmDeleteClient -> {
                _state.update { it.copy(showDeleteDialog = false) }
                deleteClient()
            }
            ViewClientIntent.DismissDeleteDialog -> _state.update { it.copy(showDeleteDialog = false) }
        }
    }

    fun collectEffects(onEffect: (ViewClientEffects) -> Unit) {
        viewModelScope.launch {
            _effects.collect { effect ->
                effect?.let {
                    onEffect(it)
                    _effects.value = null
                }
            }
        }
    }

    private fun loadClient(clientId: String) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            purchaseOrderRepository.getPurchaseOrdersByClientId(clientId)
                .catch { }
                .collect { result ->
                    if (result is DataResult.Success) {
                        _state.update { it.copy(pedidos = result.data) }
                    }
                }
        }

        when (val result = clientRepository.getClientById(clientId)) {
            is DataResult.Success -> {
                _state.update {
                    it.copy(
                        clientId = clientId,
                        name = result.data.name,
                        cpf = result.data.cpf,
                        email = result.data.email,
                        phoneNumber = result.data.phoneNumber,
                        points = result.data.points.toString(),
                        isLoading = false
                    )
                }
            }
            is DataResult.Loading -> { /* no-op */ }
            is DataResult.Error -> {
                _state.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

    private fun deleteClient() = viewModelScope.launch {
        val clientId = _state.value.clientId
        if (clientId.isEmpty()) return@launch

        when (val result = clientRepository.deleteClient(clientId)) {
            is DataResult.Success -> _effects.value = ViewClientEffects.NavigateBack
            is DataResult.Loading -> { /* no-op */ }
            is DataResult.Error -> _effects.value = ViewClientEffects.ShowMessage(result.message)
        }
    }
}