package ufsm.petsi.petservices.ui.products.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ufsm.petsi.petservices.models.Material
import ufsm.petsi.petservices.models.Product
import ufsm.petsi.petservices.models.ProductMaterial
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.implementations.MaterialRepository
import ufsm.petsi.petservices.repository.implementations.ProductRepository
import kotlin.time.ExperimentalTime

class CreateProductViewModel(
    private val productRepo: ProductRepository,
    private val materialRepo: MaterialRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateProductState())
    val state = _state.asStateFlow()

    private val _effects = Channel<CreateProductEffects>()
    val effects = _effects.receiveAsFlow()

    private var materialsJob: Job? = null

    init {
        loadAvailableMaterials()
    }

    fun handleIntent(intent: CreateProductIntent) {
        when (intent) {
            is CreateProductIntent.NameChanged ->
                _state.update { it.copy(name = intent.name, nameError = null) }

            is CreateProductIntent.QuantityChanged ->
                _state.update { it.copy(quantity = intent.quantity, quantityError = null) }

            is CreateProductIntent.CostPriceChanged ->
                _state.update { it.copy(costPrice = intent.costPrice, costPriceError = null) }

            is CreateProductIntent.SalePriceChanged ->
                _state.update { it.copy(salePrice = intent.salePrice, salePriceError = null) }

            is CreateProductIntent.MinimumStockChanged ->
                _state.update { it.copy(minimumStock = intent.stock, minimumStockError = null) }

            is CreateProductIntent.MaterialChanged ->
                _state.update { it.copy(materials = intent.materials) }

            CreateProductIntent.OpenMaterialEditor ->
                _state.update { it.copy(openMaterialEditor = true, showNewMaterialForm = false, selectedMaterial = null, materialQuantity = "", materialSearchQuery = "") }

            CreateProductIntent.CloseMaterialEditor ->
                _state.update {
                    it.copy(
                        openMaterialEditor = false,
                        showNewMaterialForm = false,
                        selectedMaterial = null,
                        materialQuantity = "",
                        materialSearchQuery = "",
                        newMaterialName = "",
                        newMaterialCostPrice = "",
                        newMaterialMinimumStock = "",
                        newMaterialMetric = "",
                        newMaterialNameError = null,
                        newMaterialCostPriceError = null,
                        newMaterialMinimumStockError = null,
                        newMaterialMetricError = null
                    )
                }

            is CreateProductIntent.ProductDeleted ->
                deleteProduct(intent.productId)

            CreateProductIntent.Cancel -> {
                val productId = _state.value.productId
                _state.value = CreateProductState()
                viewModelScope.launch {
                    if (productId.isNotEmpty()) {
                        _effects.send(CreateProductEffects.NavigateToViewProduct(productId))
                    } else {
                        _effects.send(CreateProductEffects.NavigateBack)
                    }
                }
            }

            CreateProductIntent.CreateProduct ->
                createProduct()

            CreateProductIntent.LoadAvailableMaterials ->
                loadAvailableMaterials()

            is CreateProductIntent.MaterialSearchChanged ->
                _state.update { it.copy(materialSearchQuery = intent.query) }

            is CreateProductIntent.SelectMaterial ->
                _state.update { it.copy(selectedMaterial = intent.material, materialQuantity = "") }

            is CreateProductIntent.MaterialQuantityChanged ->
                _state.update { it.copy(materialQuantity = intent.quantity) }

            CreateProductIntent.ConfirmMaterial ->
                confirmMaterial()

            is CreateProductIntent.RemoveMaterial ->
                removeMaterial(intent.idMaterial)

            CreateProductIntent.ToggleNewMaterialForm ->
                _state.update {
                    it.copy(
                        showNewMaterialForm = !it.showNewMaterialForm,
                        selectedMaterial = null,
                        materialQuantity = "",
                        materialSearchQuery = "",
                        newMaterialNameError = null,
                        newMaterialCostPriceError = null,
                        newMaterialMinimumStockError = null,
                        newMaterialMetricError = null
                    )
                }

            is CreateProductIntent.NewMaterialFieldChanged ->
                handleNewMaterialFieldChanged(intent.field, intent.value)

            CreateProductIntent.CreateAndSelectMaterial ->
                createAndSelectMaterial()
        }
    }

    fun getProduct(productId: String) {
        when (val result = productRepo.getProductById(productId)) {
            is DataResult.Success -> {
                _state.update {
                    it.copy(
                        productId = productId,
                        name = result.data.name,
                        quantity = result.data.quantity.toString(),
                        costPrice = (result.data.costPrice * 100).toLong().toString(),
                        salePrice = (result.data.salePrice * 100).toLong().toString(),
                        minimumStock = result.data.minimumStock.toString(),
                        materials = result.data.materials
                    )
                }
            }

            is DataResult.Loading -> { /* no-op */ }

            is DataResult.Error -> {
                viewModelScope.launch {
                    _effects.send(CreateProductEffects.ShowMessage(result.message))
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun createProduct() = viewModelScope.launch {
        if (!validateFields()) return@launch

        val state = _state.value
        val product = Product(
            idProduct = state.productId.ifEmpty { java.util.UUID.randomUUID().toString() },
            name = state.name,
            quantity = state.quantity.toLongOrNull() ?: 0L,
            costPrice = state.costPrice.toDoubleOrNull()?.div(100) ?: 0.0,
            salePrice = state.salePrice.toDoubleOrNull()?.div(100) ?: 0.0,
            minimumStock = state.minimumStock.toIntOrNull() ?: 0,
            materials = state.materials
        )

        val result = if (state.productId.isNotEmpty()) {
            productRepo.updateProduct(product)
        } else {
            productRepo.insertProduct(product)
        }

        when (result) {
            is DataResult.Success -> {
                _state.value = CreateProductState()
                _effects.send(CreateProductEffects.NavigateBack)
            }
            is DataResult.Loading -> { /* no-op */ }
            is DataResult.Error -> _effects.send(CreateProductEffects.ShowMessage(result.message))
        }
    }

    private fun deleteProduct(productId: String) = viewModelScope.launch {
        when (val result = productRepo.deleteProduct(productId)) {
            is DataResult.Success -> _effects.send(CreateProductEffects.NavigateBack)
            is DataResult.Loading -> { /* no-op */ }
            is DataResult.Error -> _effects.send(CreateProductEffects.ShowMessage(result.message))
        }
    }

    private fun loadAvailableMaterials() {
        materialsJob?.cancel()
        materialsJob = viewModelScope.launch {
            materialRepo.getAllMaterials().catch { e ->
                _effects.send(CreateProductEffects.ShowMessage(e.message ?: "Erro ao carregar materiais"))
            }.collect { result ->
                when (result) {
                    is DataResult.Success -> _state.update { it.copy(availableMaterials = result.data) }
                    is DataResult.Error -> _effects.send(CreateProductEffects.ShowMessage(result.message))
                    is DataResult.Loading -> { /* no-op */ }
                }
            }
        }
    }

    suspend fun loadAvailableMaterialsSync() {
        materialRepo.getAllMaterials().catch { e ->
            _effects.send(CreateProductEffects.ShowMessage(e.message ?: "Erro ao carregar materiais"))
        }.first { result ->
            if (result is DataResult.Success) {
                _state.update { it.copy(availableMaterials = result.data) }
            }
            result is DataResult.Success
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun confirmMaterial() {
        val state = _state.value
        val material = state.selectedMaterial ?: return
        val quantity = state.materialQuantity.toDoubleOrNull() ?: return

        val productMaterial = ProductMaterial(
            idProduct = state.productId.ifEmpty { "" },
            idMaterial = material.idMaterial,
            quantity = quantity
        )

        val updatedMaterials = state.materials + productMaterial
        _state.update {
            it.copy(
                materials = updatedMaterials,
                openMaterialEditor = false,
                selectedMaterial = null,
                materialQuantity = "",
                materialSearchQuery = ""
            )
        }
    }

    private fun removeMaterial(idMaterial: String) {
        _state.update { current ->
            current.copy(
                materials = current.materials.filter { it.idMaterial != idMaterial }
            )
        }
    }

    private fun handleNewMaterialFieldChanged(field: NewMaterialField, value: String) {
        _state.update { current ->
            when (field) {
                NewMaterialField.NAME -> current.copy(newMaterialName = value, newMaterialNameError = null)
                NewMaterialField.COST_PRICE -> current.copy(newMaterialCostPrice = value, newMaterialCostPriceError = null)
                NewMaterialField.MINIMUM_STOCK -> current.copy(newMaterialMinimumStock = value, newMaterialMinimumStockError = null)
                NewMaterialField.METRIC -> current.copy(newMaterialMetric = value, newMaterialMetricError = null)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun createAndSelectMaterial() = viewModelScope.launch {
        val state = _state.value

        var hasError = false
        val nameError = if (state.newMaterialName.isBlank()) { hasError = true; "Nome é obrigatório" } else null
        val costPriceError = if (state.newMaterialCostPrice.isBlank()) { hasError = true; "Valor é obrigatório" } else null
        val minimumStockError = if (state.newMaterialMinimumStock.isBlank()) { hasError = true; "Estoque é obrigatório" } else null
        val metricError = if (state.newMaterialMetric.isBlank()) { hasError = true; "Métrica é obrigatória" } else null

        if (hasError) {
            _state.update {
                it.copy(
                    newMaterialNameError = nameError,
                    newMaterialCostPriceError = costPriceError,
                    newMaterialMinimumStockError = minimumStockError,
                    newMaterialMetricError = metricError
                )
            }
            return@launch
        }

        val material = Material(
            name = state.newMaterialName,
            costPrice = state.newMaterialCostPrice.toDoubleOrNull() ?: 0.0,
            minimumStock = state.newMaterialMinimumStock.toIntOrNull() ?: 0,
            metric = state.newMaterialMetric
        )

        when (val result = materialRepo.insertMaterial(material)) {
            is DataResult.Success -> {
                val updatedMaterials = _state.value.availableMaterials + material
                _state.update {
                    it.copy(
                        availableMaterials = updatedMaterials,
                        selectedMaterial = material,
                        showNewMaterialForm = false,
                        newMaterialName = "",
                        newMaterialCostPrice = "",
                        newMaterialMinimumStock = "",
                        newMaterialMetric = "",
                        newMaterialNameError = null,
                        newMaterialCostPriceError = null,
                        newMaterialMinimumStockError = null,
                        newMaterialMetricError = null
                    )
                }
            }
            is DataResult.Error -> _effects.send(CreateProductEffects.ShowMessage(result.message))
            is DataResult.Loading -> { /* no-op */ }
        }
    }

    private fun validateFields(): Boolean {
        val state = _state.value
        var hasError = false

        val nameError = if (state.name.isBlank()) { hasError = true; "Nome é obrigatório" } else null
        val costPriceError = if (state.costPrice.isBlank()) { hasError = true; "Valor de compra é obrigatório" } else null
        val salePriceError = if (state.salePrice.isBlank()) { hasError = true; "Valor de venda é obrigatório" } else null

        _state.update {
            it.copy(
                nameError = nameError,
                costPriceError = costPriceError,
                salePriceError = salePriceError
            )
        }

        return !hasError
    }
}
