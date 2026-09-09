package ufsm.petsi.petservices.ui.clients.pedido

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ufsm.petsi.petservices.models.PurchaseOrder
import ufsm.petsi.petservices.models.PurchaseOrderProduct
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.implementations.ProductRepository
import ufsm.petsi.petservices.repository.implementations.PurchaseOrderRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class CreatePedidoViewModel(
    private val productRepo: ProductRepository,
    private val purchaseOrderRepo: PurchaseOrderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreatePedidoState())
    val state = _state.asStateFlow()

    private val _effects = Channel<CreatePedidoEffects>()
    val effects = _effects.receiveAsFlow()

    private var productsJob: Job? = null

    init {
        _state.update { it.copy(date = todayDate()) }
        loadAvailableProducts()
    }

    fun handleIntent(intent: CreatePedidoIntent) {
        when (intent) {
            is CreatePedidoIntent.DateChanged ->
                _state.update { it.copy(date = intent.date, dateError = null) }

            CreatePedidoIntent.OpenProductPicker -> {
                _state.update {
                    it.copy(
                        openProductPicker = true,
                        productSearchQuery = "",
                        selectedProduct = null,
                        productQuantity = "",
                        quantityError = null
                    )
                }
                loadAvailableProducts()
            }

            CreatePedidoIntent.CloseProductPicker ->
                _state.update {
                    it.copy(
                        openProductPicker = false,
                        productSearchQuery = "",
                        selectedProduct = null,
                        productQuantity = "",
                        quantityError = null
                    )
                }

            is CreatePedidoIntent.ProductSearchChanged ->
                _state.update { it.copy(productSearchQuery = intent.query) }

            is CreatePedidoIntent.SelectProduct ->
                _state.update { it.copy(selectedProduct = intent.product, productQuantity = "", quantityError = null) }

            is CreatePedidoIntent.QuantityChanged ->
                _state.update {
                    it.copy(
                        productQuantity = intent.quantity.filter { c -> c.isDigit() }.take(5),
                        quantityError = null
                    )
                }

            CreatePedidoIntent.ConfirmProduct -> confirmProduct()

            is CreatePedidoIntent.RemoveProduct -> removeProduct(intent.idProduct)

            CreatePedidoIntent.Submit -> submit()

            CreatePedidoIntent.Cancel -> {
                viewModelScope.launch { _effects.send(CreatePedidoEffects.NavigateBack) }
            }
        }
    }

    fun setClient(clientId: String) {
        _state.update { it.copy(clientId = clientId) }
    }

    fun reset() {
        _state.value = CreatePedidoState().copy(date = todayDate())
    }

    private fun loadAvailableProducts() {
        productsJob?.cancel()
        _state.update { it.copy(isProductsLoading = true, productsErrorMessage = null) }
        productsJob = viewModelScope.launch {
            productRepo.getAllProducts().catch { e ->
                _state.update {
                    it.copy(
                        isProductsLoading = false,
                        productsErrorMessage = e.message ?: "Erro ao carregar produtos"
                    )
                }
            }.collect { result ->
                when (result) {
                    is DataResult.Success -> _state.update {
                        it.copy(availableProducts = result.data, isProductsLoading = false)
                    }
                    is DataResult.Error -> _state.update {
                        it.copy(
                            isProductsLoading = false,
                            productsErrorMessage = result.message
                        )
                    }
                    is DataResult.Loading -> { /* no-op */ }
                }
            }
        }
    }

    private fun confirmProduct() {
        val state = _state.value
        val product = state.selectedProduct ?: return
        val quantity = state.productQuantity.toIntOrNull()

        if (quantity == null || quantity <= 0) {
            _state.update { it.copy(quantityError = "Quantidade deve ser maior que zero") }
            return
        }

        val existingIndex = state.products.indexOfFirst { it.idProduct == product.idProduct }
        val updatedProducts = if (existingIndex >= 0) {
            state.products.toMutableList().apply {
                set(
                    existingIndex,
                    state.products[existingIndex].copy(quantity = quantity)
                )
            }
        } else {
            state.products + PurchaseOrderProduct(
                idPurchaseOrder = "",
                idProduct = product.idProduct,
                quantity = quantity
            )
        }

        _state.update {
            it.copy(
                products = updatedProducts,
                openProductPicker = false,
                selectedProduct = null,
                productQuantity = "",
                productSearchQuery = "",
                quantityError = null,
                productsError = null
            )
        }
    }

    private fun removeProduct(idProduct: String) {
        _state.update { current ->
            current.copy(products = current.products.filter { it.idProduct != idProduct })
        }
    }

    private fun submit() = viewModelScope.launch {
        val state = _state.value
        if (!validate()) return@launch

        val orderId = java.util.UUID.randomUUID().toString()
        val order = PurchaseOrder(
            idPurchaseOrder = orderId,
            idClient = state.clientId,
            date = state.date.trim()
        )
        val lines = state.products.map { it.copy(idPurchaseOrder = orderId) }

        when (val result = purchaseOrderRepo.insertPurchaseOrder(order, lines)) {
            is DataResult.Success -> {
                _state.value = CreatePedidoState().copy(date = todayDate())
                _effects.send(CreatePedidoEffects.NavigateBack)
            }
            is DataResult.Loading -> { /* no-op */ }
            is DataResult.Error -> _effects.send(CreatePedidoEffects.ShowMessage(result.message))
        }
    }

    private fun validate(): Boolean {
        val state = _state.value
        var hasError = false

        val dateError = if (state.date.isBlank()) { hasError = true; "Data é obrigatória" } else null
        val productsError = if (state.products.isEmpty()) { hasError = true; "Adicione ao menos um produto" } else null

        _state.update { it.copy(dateError = dateError, productsError = productsError) }

        return !hasError
    }

    private fun todayDate(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val dd = now.date.day.toString().padStart(2, '0')
        val mm = (now.date.month.ordinal + 1).toString().padStart(2, '0')
        return "$dd/$mm/${now.date.year}"
    }
}