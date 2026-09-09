package ufsm.petsi.petservices.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.implementations.ProductRepository
import ufsm.petsi.petservices.models.Product
import ufsm.petsi.petservices.session.SessionManager
import kotlin.time.ExperimentalTime

class ProductsViewModel(
    private val repository: ProductRepository,
    val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductsState())
    val uiState = _uiState.asStateFlow()

    private val _effects = Channel<ProductsEffects>()
    val effects = _effects.receiveAsFlow()

    init {
        observeProducts()
    }

    @OptIn(ExperimentalTime::class)
    fun handleIntent(intent: ProductsIntent) = viewModelScope.launch {
        when (intent) {
            is ProductsIntent.OnProductClick -> {
                _effects.send(ProductsEffects.NavigateToProductDetails(intent.productId))
            }

            is ProductsIntent.OnProductEdit -> {
                _effects.send(ProductsEffects.NavigateToProductEdit(intent.productId))
            }

            is ProductsIntent.OnProductDuplicate -> {
                val product = _uiState.value.products.find { it.idProduct == intent.productId }
                product?.let { duplicateProduct(it) }
            }

            is ProductsIntent.OnAddProductClick -> {
                _effects.send(ProductsEffects.NavigateToProductCreation)
            }

            is ProductsIntent.OnProductDelete -> {
                val product = _uiState.value.products.find { it.idProduct == intent.productId }
                _uiState.update { it.copy(showDeleteDialog = true, productToDelete = product) }
            }

            ProductsIntent.ConfirmDeleteProduct -> {
                val product = _uiState.value.productToDelete ?: return@launch
                _uiState.update { it.copy(showDeleteDialog = false, productToDelete = null) }
                when (val result = repository.deleteProduct(product.idProduct)) {
                    is DataResult.Error -> _effects.send(ProductsEffects.NavigateToProductCreation)
                    else -> { /* no-op */ }
                }
            }

            ProductsIntent.DismissDeleteDialog -> {
                _uiState.update { it.copy(showDeleteDialog = false, productToDelete = null) }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun duplicateProduct(product: Product) = viewModelScope.launch {
        val copy = Product(
            name = "${product.name} (Cópia)",
            quantity = product.quantity,
            costPrice = product.costPrice,
            salePrice = product.salePrice,
            minimumStock = product.minimumStock,
            soldQuantity = product.soldQuantity,
            materials = product.materials
        )
        when (val result = repository.insertProduct(copy)) {
            is DataResult.Error -> _effects.send(ProductsEffects.NavigateToProductCreation)
            else -> { /* no-op */ }
        }
    }

    private fun observeProducts() = viewModelScope.launch {
        repository.getAllProducts().collect { result ->
            when (result) {
                is DataResult.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            products = result.data,
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
}
