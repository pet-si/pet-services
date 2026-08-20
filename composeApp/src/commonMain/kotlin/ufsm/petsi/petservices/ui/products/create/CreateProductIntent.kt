package ufsm.petsi.petservices.ui.products.create

import ufsm.petsi.petservices.models.Material
import ufsm.petsi.petservices.models.ProductMaterial

sealed interface CreateProductIntent {
    data class NameChanged(val name: String) : CreateProductIntent
    data class QuantityChanged(val quantity: String) : CreateProductIntent
    data class CostPriceChanged(val costPrice: String) : CreateProductIntent
    data class SalePriceChanged(val salePrice: String) : CreateProductIntent
    data class MinimumStockChanged(val stock: String) : CreateProductIntent
    data class ProductDeleted(val productId: String) : CreateProductIntent
    data class MaterialChanged(val materials: List<ProductMaterial>) : CreateProductIntent
    data object OpenMaterialEditor : CreateProductIntent
    data object CloseMaterialEditor : CreateProductIntent
    data object Cancel : CreateProductIntent
    data object CreateProduct : CreateProductIntent

    data object LoadAvailableMaterials : CreateProductIntent
    data class MaterialSearchChanged(val query: String) : CreateProductIntent
    data class SelectMaterial(val material: Material) : CreateProductIntent
    data class MaterialQuantityChanged(val quantity: String) : CreateProductIntent
    data object ConfirmMaterial : CreateProductIntent
    data class RemoveMaterial(val idMaterial: String) : CreateProductIntent
    data object ToggleNewMaterialForm : CreateProductIntent
    data class NewMaterialFieldChanged(val field: NewMaterialField, val value: String) : CreateProductIntent
    data object CreateAndSelectMaterial : CreateProductIntent
}

enum class NewMaterialField {
    NAME, COST_PRICE, MINIMUM_STOCK, METRIC
}

data class CreateProductState(
    val name: String = "",
    val quantity: String = "",
    val costPrice: String = "",
    val salePrice: String = "",
    val minimumStock: String = "",
    val materials: List<ProductMaterial> = emptyList(),
    val productId: String = "",
    val openMaterialEditor: Boolean = false,

    val nameError: String? = null,
    val quantityError: String? = null,
    val costPriceError: String? = null,
    val salePriceError: String? = null,
    val minimumStockError: String? = null,

    val availableMaterials: List<Material> = emptyList(),
    val selectedMaterial: Material? = null,
    val materialQuantity: String = "",
    val materialSearchQuery: String = "",
    val showNewMaterialForm: Boolean = false,
    val newMaterialName: String = "",
    val newMaterialCostPrice: String = "",
    val newMaterialMinimumStock: String = "",
    val newMaterialMetric: String = "",

    val newMaterialNameError: String? = null,
    val newMaterialCostPriceError: String? = null,
    val newMaterialMinimumStockError: String? = null,
    val newMaterialMetricError: String? = null,
)

sealed interface CreateProductEffects {
    data object NavigateBack : CreateProductEffects
    data class NavigateToViewProduct(val productId: String) : CreateProductEffects
    data class ShowMessage(val message: String) : CreateProductEffects
}
