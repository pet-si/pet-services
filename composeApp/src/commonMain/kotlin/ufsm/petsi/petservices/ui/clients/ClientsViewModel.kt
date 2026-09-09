package ufsm.petsi.petservices.ui.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ufsm.petsi.petservices.models.Client
import ufsm.petsi.petservices.models.Product
import ufsm.petsi.petservices.models.PurchaseOrderProduct
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.implementations.ClientRepository
import ufsm.petsi.petservices.repository.implementations.ProductRepository
import ufsm.petsi.petservices.repository.implementations.PurchaseOrderRepository

class ClientsViewModel(
    private val clientRepository: ClientRepository,
    private val purchaseOrderRepository: PurchaseOrderRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientsState())
    val uiState = _uiState.asStateFlow()

    private val _effects = Channel<ClientsEffects>()
    val effects = _effects.receiveAsFlow()

    init {
        observeClients()
        observePurchaseOrders()
        observePurchaseOrderProducts()
        observeProducts()
    }

    fun handleIntent(intent: ClientsIntent) = viewModelScope.launch {
        when (intent) {
            is ClientsIntent.OnClientClick ->
                toggleClient(intent.clientId)

            is ClientsIntent.OnClientView ->
                _effects.send(ClientsEffects.NavigateToClientDetails(intent.clientId))

            is ClientsIntent.OnClientEdit ->
                _effects.send(ClientsEffects.NavigateToClientEdit(intent.clientId))

            ClientsIntent.OnAddClientClick ->
                _effects.send(ClientsEffects.NavigateToClientCreation)

            is ClientsIntent.OnClientDelete -> {
                val client = _uiState.value.clients.find { it.idClient == intent.clientId }
                _uiState.update { it.copy(showDeleteDialog = true, clientToDelete = client) }
            }

            ClientsIntent.ConfirmDeleteClient -> {
                val client = _uiState.value.clientToDelete ?: return@launch
                _uiState.update { it.copy(showDeleteDialog = false, clientToDelete = null) }
                when (val result = clientRepository.deleteClient(client.idClient)) {
                    is DataResult.Error -> { /* no-op */ }
                    else -> { /* no-op */ }
                }
            }

            ClientsIntent.DismissDeleteDialog -> {
                _uiState.update { it.copy(showDeleteDialog = false, clientToDelete = null) }
            }
        }
    }

    private fun toggleClient(clientId: String) {
        _uiState.update { state ->
            state.copy(expandedClientId = if (state.expandedClientId == clientId) null else clientId)
        }
    }

    private fun observeClients() = viewModelScope.launch {
        clientRepository.getAllClients().collect { result ->
            when (result) {
                is DataResult.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            clients = result.data,
                            errorMessage = null
                        )
                    }
                }

                is DataResult.Error -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }

                DataResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun observePurchaseOrders() = viewModelScope.launch {
        purchaseOrderRepository.getAllPurchaseOrders().collect { result ->
            when (result) {
                is DataResult.Success -> {
                    _uiState.update { state ->
                        state.copy(pedidosByClient = result.data.groupBy { it.idClient })
                    }
                }

                is DataResult.Error -> { /* no-op */ }

                DataResult.Loading -> { /* no-op */ }
            }
        }
    }

    private fun observePurchaseOrderProducts() = viewModelScope.launch {
        purchaseOrderRepository.getAllPurchaseOrderProducts().collect { result ->
            when (result) {
                is DataResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            purchaseOrderProductsByOrder = result.data.groupBy { it.idPurchaseOrder }
                        )
                    }
                }

                is DataResult.Error -> { /* no-op */ }

                DataResult.Loading -> { /* no-op */ }
            }
        }
    }

    private fun observeProducts() = viewModelScope.launch {
        productRepository.getAllProducts().collect { result ->
            when (result) {
                is DataResult.Success -> {
                    _uiState.update { state ->
                        state.copy(productsById = result.data.associateBy { it.idProduct })
                    }
                }

                is DataResult.Error -> { /* no-op */ }

                DataResult.Loading -> { /* no-op */ }
            }
        }
    }
}