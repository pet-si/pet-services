package ufsm.petsi.petservices.ui.clients.pedido

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.implementations.ProductRepository
import ufsm.petsi.petservices.repository.implementations.PurchaseOrderRepository

class ViewPedidoViewModel(
    private val purchaseOrderRepo: PurchaseOrderRepository,
    private val productRepo: ProductRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ViewPedidoState())
    val state = _state.asStateFlow()

    private var loadJob: Job? = null
    private var productsJob: Job? = null

    init {
        observeProducts()
    }

    fun handleIntent(intent: ViewPedidoIntent) {
        when (intent) {
            is ViewPedidoIntent.LoadPedido -> loadPedido(intent.orderId, intent.clientName)
        }
    }

    private fun loadPedido(orderId: String, clientName: String = "") {
        _state.update {
            it.copy(
                orderId = orderId,
                clientName = clientName,
                date = "",
                products = emptyList(),
                isLoading = true,
                errorMessage = null
            )
        }

        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            when (val result = purchaseOrderRepo.getPurchaseOrderById(orderId)) {
                is DataResult.Success -> _state.update { it.copy(date = result.data.date) }
                is DataResult.Error -> _state.update { it.copy(errorMessage = result.message, isLoading = false) }
                is DataResult.Loading -> { /* no-op */ }
            }

            purchaseOrderRepo.getPurchaseOrderProductsByOrderId(orderId)
                .catch { e ->
                    _state.update { it.copy(errorMessage = e.message ?: "Erro ao carregar pedido", isLoading = false) }
                }
                .collect { result ->
                    when (result) {
                        is DataResult.Success ->
                            _state.update { it.copy(products = result.data, isLoading = false) }
                        is DataResult.Error ->
                            _state.update { it.copy(errorMessage = result.message, isLoading = false) }
                        is DataResult.Loading -> { /* no-op */ }
                    }
                }
        }
    }

    private fun observeProducts() {
        productsJob?.cancel()
        productsJob = viewModelScope.launch {
            productRepo.getAllProducts().catch { }.collect { result ->
                if (result is DataResult.Success) {
                    _state.update { it.copy(availableProducts = result.data) }
                }
            }
        }
    }
}