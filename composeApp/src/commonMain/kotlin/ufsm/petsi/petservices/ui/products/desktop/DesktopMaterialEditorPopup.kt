package ufsm.petsi.petservices.ui.products.desktop

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ufsm.petsi.petservices.ui.components.dialog.ModalPopup
import ufsm.petsi.petservices.ui.components.material.MaterialForm
import ufsm.petsi.petservices.ui.components.material.MaterialPicker
import ufsm.petsi.petservices.ui.components.textField.DefaultTextField
import ufsm.petsi.petservices.ui.products.create.CreateProductIntent
import ufsm.petsi.petservices.ui.products.create.CreateProductState
import ufsm.petsi.petservices.ui.products.create.NewMaterialField

@Composable
fun DesktopMaterialEditorPopup(
    state: CreateProductState,
    onAction: (CreateProductIntent) -> Unit,
    onDismiss: () -> Unit
) {
    ModalPopup(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (state.showNewMaterialForm) "Novo Material" else "Adicionar Material",
                style = MaterialTheme.typography.titleLarge
            )
        },
        buttons = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
            if (state.showNewMaterialForm) {
                Button(
                    onClick = { onAction(CreateProductIntent.CreateAndSelectMaterial) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Criar e Selecionar")
                }
            } else {
                Button(
                    onClick = { onAction(CreateProductIntent.ConfirmMaterial) },
                    enabled = state.selectedMaterial != null &&
                        state.materialQuantity.toDoubleOrNull() != null,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirmar")
                }
            }
        }
    ) {
        if (state.showNewMaterialForm) {
            MaterialForm(
                name = state.newMaterialName,
                onNameChange = {
                    onAction(CreateProductIntent.NewMaterialFieldChanged(NewMaterialField.NAME, it))
                },
                costPrice = state.newMaterialCostPrice,
                onCostPriceChange = {
                    onAction(CreateProductIntent.NewMaterialFieldChanged(NewMaterialField.COST_PRICE, it))
                },
                minimumStock = state.newMaterialMinimumStock,
                onMinimumStockChange = {
                    onAction(CreateProductIntent.NewMaterialFieldChanged(NewMaterialField.MINIMUM_STOCK, it))
                },
                metric = state.newMaterialMetric,
                onMetricChange = {
                    onAction(CreateProductIntent.NewMaterialFieldChanged(NewMaterialField.METRIC, it))
                },
                nameError = state.newMaterialNameError,
                costPriceError = state.newMaterialCostPriceError,
                minimumStockError = state.newMaterialMinimumStockError,
                metricError = state.newMaterialMetricError
            )
        } else {
            MaterialPicker(
                materials = state.availableMaterials.filter {
                    it.name.contains(state.materialSearchQuery, ignoreCase = true)
                },
                searchQuery = state.materialSearchQuery,
                onSearchQueryChange = {
                    onAction(CreateProductIntent.MaterialSearchChanged(it))
                },
                onMaterialSelected = { onAction(CreateProductIntent.SelectMaterial(it)) },
                selectedMaterialId = state.selectedMaterial?.idMaterial,
                showTitle = false
            )

            state.selectedMaterial?.let {
                DefaultTextField(
                    label = { Text("Quantidade") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    value = state.materialQuantity,
                    onValueChange = { onAction(CreateProductIntent.MaterialQuantityChanged(it)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        FilledTonalButton(
            onClick = { onAction(CreateProductIntent.ToggleNewMaterialForm) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (state.showNewMaterialForm) "Buscar material existente"
                else "Criar novo material"
            )
        }
    }
}