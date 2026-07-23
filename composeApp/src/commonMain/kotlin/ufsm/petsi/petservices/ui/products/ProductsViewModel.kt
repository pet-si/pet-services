package ufsm.petsi.petservices.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ufsm.petsi.petservices.models.Material
import ufsm.petsi.petservices.models.Product
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.implementations.ProductRepository
import ufsm.petsi.petservices.session.SessionManager
import kotlin.time.ExperimentalTime

class ProductsViewModel(
    private val repository: ProductRepository,
    val sessionManager: SessionManager
) :
    ViewModel() {
    private val _uiState = MutableStateFlow<ProductsState>(ProductsState())
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

            is ProductsIntent.OnAddProductClick -> {
                repository.insertProduct(
                    Product(
                        idProduct = "teste",
                        name = "Novo Produto",
                        costPrice = 2.5,
                        salePrice = 5.0,
                    )
                )
            }
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
                    // send an error toast using effects
                }

                DataResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

}