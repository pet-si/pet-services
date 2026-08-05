package ufsm.petsi.petservices.ui.products.create

import ufsm.petsi.petservices.models.ProductMaterial

sealed interface CreateProductIntent {
    data class NameChanged(val name: String) : CreateProductIntent
    data class QuantityChanged(val quantity: Long) : CreateProductIntent
    data class CostPriceChanged(val costPrice: Double) : CreateProductIntent
    data class SalePriceChanged(val salePrice: Double) : CreateProductIntent
    data class MinimumStockChanged(val stock: Int) : CreateProductIntent
    data class ProductDeleted(val productId: String) : CreateProductIntent
    data class MaterialChanged(val materials: List<ProductMaterial>) : CreateProductIntent
    data object OpenMaterialEditor : CreateProductIntent
    data object Cancel : CreateProductIntent
    data object CreateProduct : CreateProductIntent
}

data class CreateProductState(
    val name: String = "",
    val quantity: Long? = 0L,
    val costPrice: Double = 0.0,
    val salePrice: Double = 0.0,
    val minimumStock: Int = 0,
    val materials: List<ProductMaterial> = emptyList(),
    val productId: String = "",
    val openMaterialEditor: Boolean = false
)

sealed interface CreateProductEffects {
    data object NavigateBack : CreateProductEffects
    data class ShowMessage(val message: String) : CreateProductEffects
}