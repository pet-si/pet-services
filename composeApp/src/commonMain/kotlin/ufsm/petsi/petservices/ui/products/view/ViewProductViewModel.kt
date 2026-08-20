package ufsm.petsi.petservices.ui.products.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.implementations.MaterialRepository
import ufsm.petsi.petservices.repository.implementations.ProductRepository

class ViewProductViewModel(
    private val productRepo: ProductRepository,
    private val materialRepo: MaterialRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ViewProductState())
    val state = _state.asStateFlow()

    private val _effects = MutableStateFlow<ViewProductEffects?>(null)

    fun handleIntent(intent: ViewProductIntent) {
        when (intent) {
            is ViewProductIntent.LoadProduct -> loadProduct(intent.productId)
            ViewProductIntent.DeleteProduct -> _state.update { it.copy(showDeleteDialog = true) }
            ViewProductIntent.ConfirmDeleteProduct -> {
                _state.update { it.copy(showDeleteDialog = false) }
                deleteProduct()
            }
            ViewProductIntent.DismissDeleteDialog -> _state.update { it.copy(showDeleteDialog = false) }
        }
    }

    fun collectEffects(onEffect: (ViewProductEffects) -> Unit) {
        viewModelScope.launch {
            _effects.collect { effect ->
                effect?.let {
                    onEffect(it)
                    _effects.value = null
                }
            }
        }
    }

    private fun loadProduct(productId: String) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            materialRepo.getAllMaterials().catch { }.collect { result ->
                if (result is DataResult.Success) {
                    _state.update { it.copy(availableMaterials = result.data) }
                }
            }
        }

        when (val result = productRepo.getProductById(productId)) {
            is DataResult.Success -> {
                _state.update {
                    it.copy(
                        productId = productId,
                        name = result.data.name,
                        quantity = result.data.quantity.toString(),
                        costPrice = "%.2f".format(result.data.costPrice),
                        salePrice = "%.2f".format(result.data.salePrice),
                        minimumStock = result.data.minimumStock.toString(),
                        soldQuantity = result.data.soldQuantity.toString(),
                        materials = result.data.materials,
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

    private fun deleteProduct() = viewModelScope.launch {
        val productId = _state.value.productId
        if (productId.isEmpty()) return@launch

        when (val result = productRepo.deleteProduct(productId)) {
            is DataResult.Success -> _effects.value = ViewProductEffects.NavigateBack
            is DataResult.Loading -> { /* no-op */ }
            is DataResult.Error -> _effects.value = ViewProductEffects.ShowMessage(result.message)
        }
    }
}
