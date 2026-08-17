package ufsm.petsi.petservices.ui.products.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ufsm.petsi.petservices.models.Product
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.implementations.MaterialRepository
import ufsm.petsi.petservices.repository.implementations.ProductRepository
import ufsm.petsi.petservices.ui.products.ProductsEffects
import kotlin.time.ExperimentalTime

class CreateProductViewModel(
    val productRepo: ProductRepository,
    val materialRepo: MaterialRepository
) : ViewModel() {

    private val _state = MutableStateFlow<CreateProductState>(CreateProductState())
    val state = _state.asStateFlow()

    private val _effects = Channel<CreateProductEffects>()
    val effects = _effects.receiveAsFlow()


    fun handleIntent(intent: CreateProductIntent) {
        when (intent) {
            is CreateProductIntent.NameChanged -> {
                println(intent.name)
                _state.update { it.copy(name = intent.name, nameError = null) }
            }

            is CreateProductIntent.QuantityChanged -> {
                _state.update { it.copy(quantity = intent.quantity, quantityError = null) }
            }

            is CreateProductIntent.CostPriceChanged -> {
                _state.update { it.copy(costPrice = intent.costPrice, costPriceError = null) }
            }

            is CreateProductIntent.SalePriceChanged -> {
                _state.update { it.copy(salePrice = intent.salePrice, salePriceError = null) }
            }

            is CreateProductIntent.MinimumStockChanged -> {
                _state.update { it.copy(minimumStock = intent.stock, minimumStockError = null) }
            }

            is CreateProductIntent.MaterialChanged -> {
                _state.update { it.copy(materials = intent.materials) }
            }

            is CreateProductIntent.OpenMaterialEditor -> {
                _state.update { it.copy(openMaterialEditor = true) }
            }

            is CreateProductIntent.ProductDeleted -> {
                // Handle delete product
            }

            CreateProductIntent.Cancel -> {
                viewModelScope.launch {
                    _effects.send(CreateProductEffects.NavigateBack)
                }
            }

            CreateProductIntent.CreateProduct -> {
                createProduct()
            }
        }
    }

    fun getProduct(productId: String) {
        when (val result = productRepo.getProductById(productId)) {
            is DataResult.Success -> {
                _state.value = _state.value.copy(
                    name = result.data.name,
                    quantity = result.data.quantity.toString(),
                    costPrice = result.data.costPrice.toString(),
                    salePrice = result.data.salePrice.toString(),
                    minimumStock = result.data.minimumStock.toString(),
                    materials = result.data.materials
                )
            }

            DataResult.Loading -> TODO()
            is DataResult.Error -> TODO()
        }
    }

    @OptIn(ExperimentalTime::class)
    fun createProduct() = viewModelScope.launch {
        validateFields()
        val state = _state.value
        val product = Product(
            name = state.name,
            quantity = state.quantity.toLong(),
            costPrice = state.costPrice.toDouble(),
            salePrice = state.salePrice.toDouble(),
            minimumStock = state.minimumStock.toInt(),
            materials = state.materials
        )
        when (productRepo.insertProduct(product)) {
            is DataResult.Success -> {
                _effects.send(CreateProductEffects.NavigateBack)
            }

            DataResult.Loading -> TODO()
            is DataResult.Error -> TODO()
        }
    }

    private fun validateFields() {
        val state = _state.value
    }

}