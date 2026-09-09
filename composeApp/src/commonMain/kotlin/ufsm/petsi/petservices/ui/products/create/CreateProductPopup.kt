package ufsm.petsi.petservices.ui.products.create

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.outlined.Close
import org.koin.compose.viewmodel.koinViewModel
import ufsm.petsi.petservices.ui.components.dialog.ModalPopup
import ufsm.petsi.petservices.ui.components.form.ProductFormFields
import ufsm.petsi.petservices.ui.components.material.MaterialSection

@Composable
fun CreateProductPopup(
    productId: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: CreateProductViewModel = koinViewModel()
    val state = viewModel.state.collectAsState().value

    LaunchedEffect(productId) {
        if (productId != null) {
            viewModel.getProduct(productId)
        } else {
            viewModel.resetForCreate()
        }
        viewModel.loadAvailableMaterialsSync()
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                CreateProductEffects.NavigateBack,
                is CreateProductEffects.NavigateToViewProduct -> onDismiss()

                is CreateProductEffects.ShowMessage -> Unit
            }
        }
    }

    val isEdit = productId != null

    ModalPopup(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEdit) "Editar Produto" else "Criar Produto",
                style = MaterialTheme.typography.titleLarge
            )
        },
        buttons = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
            Button(
                onClick = { viewModel.handleIntent(CreateProductIntent.CreateProduct) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(if (isEdit) "Salvar" else "Criar")
            }
        },
        modifier = modifier
    ) {
        ProductFormFields(
            name = state.name,
            onNameChange = { viewModel.handleIntent(CreateProductIntent.NameChanged(it)) },
            quantity = state.quantity,
            onQuantityChange = { viewModel.handleIntent(CreateProductIntent.QuantityChanged(it)) },
            minimumStock = state.minimumStock,
            onMinimumStockChange = { viewModel.handleIntent(CreateProductIntent.MinimumStockChanged(it)) },
            costPrice = state.costPrice,
            onCostPriceChange = { viewModel.handleIntent(CreateProductIntent.CostPriceChanged(it)) },
            salePrice = state.salePrice,
            onSalePriceChange = { viewModel.handleIntent(CreateProductIntent.SalePriceChanged(it)) },
            nameError = state.nameError,
            costPriceError = state.costPriceError,
            salePriceError = state.salePriceError
        )

        MaterialSection(
            selectedMaterials = state.materials,
            availableMaterials = state.availableMaterials,
            modifier = Modifier.padding(top = 16.dp),
            onAddMaterial = { viewModel.handleIntent(CreateProductIntent.OpenMaterialEditor) },
            trailing = { productMaterial ->
                IconButton(
                    onClick = {
                        viewModel.handleIntent(
                            CreateProductIntent.RemoveMaterial(productMaterial.idMaterial)
                        )
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = MaterialIcons.Outlined.Close,
                        contentDescription = "Remover material",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        )
    }

    if (state.openMaterialEditor) {
        MaterialEditorPopup(
            state = state,
            onAction = { viewModel.handleIntent(it) },
            onDismiss = { viewModel.handleIntent(CreateProductIntent.CloseMaterialEditor) }
        )
    }
}