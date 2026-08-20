package ufsm.petsi.petservices.ui.products.view

import ufsm.petsi.petservices.models.Material
import ufsm.petsi.petservices.models.ProductMaterial

sealed interface ViewProductIntent {
    data class LoadProduct(val productId: String) : ViewProductIntent
    data object DeleteProduct : ViewProductIntent
    data object ConfirmDeleteProduct : ViewProductIntent
    data object DismissDeleteDialog : ViewProductIntent
}

data class ViewProductState(
    val productId: String = "",
    val name: String = "",
    val quantity: String = "",
    val costPrice: String = "",
    val salePrice: String = "",
    val minimumStock: String = "",
    val soldQuantity: String = "",
    val materials: List<ProductMaterial> = emptyList(),
    val availableMaterials: List<Material> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showDeleteDialog: Boolean = false,
)

sealed interface ViewProductEffects {
    data object NavigateBack : ViewProductEffects
    data class NavigateToEdit(val productId: String) : ViewProductEffects
    data class ShowMessage(val message: String) : ViewProductEffects
}
