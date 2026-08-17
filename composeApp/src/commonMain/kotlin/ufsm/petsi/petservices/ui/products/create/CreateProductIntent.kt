package ufsm.petsi.petservices.ui.products.create

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
    data object Cancel : CreateProductIntent
    data object CreateProduct : CreateProductIntent
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

    val nameError : String? = null,
    val quantityError : String? = null,
    val costPriceError : String? = null,
    val salePriceError : String? = null,
    val minimumStockError: String? = null,
)

sealed interface CreateProductEffects {
    data object NavigateBack : CreateProductEffects
    data class ShowMessage(val message: String) : CreateProductEffects
}