package ufsm.petsi.petservices.ui.products.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import ufsm.petsi.petservices.ui.components.dialog.ModalPopup
import ufsm.petsi.petservices.ui.components.material.MaterialSection

@Composable
fun ProductViewPopup(
    productId: String,
    onDismiss: () -> Unit,
    onEdit: (String) -> Unit
) {
    val viewModel: ViewProductViewModel = koinViewModel()
    val state = viewModel.state.collectAsState().value

    LaunchedEffect(productId) {
        viewModel.handleIntent(ViewProductIntent.LoadProduct(productId))
    }

    ModalPopup(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = state.name.ifEmpty { "Produto" },
                style = MaterialTheme.typography.titleLarge
            )
        },
        buttons = {
            TextButton(onClick = onDismiss) { Text("Fechar") }
            Button(
                onClick = { onEdit(state.productId) },
                enabled = state.productId.isNotEmpty(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Editar")
            }
        }
    ) {
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Carregando produto...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else if (state.errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.errorMessage.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            DetailRow(label = "Quantidade em Estoque", value = state.quantity)
            DetailRow(label = "Valor de Compra", value = "R$ ${state.costPrice}")
            DetailRow(label = "Valor de Venda", value = "R$ ${state.salePrice}")
            DetailRow(label = "Estoque Mínimo", value = state.minimumStock)
            DetailRow(label = "Quantidade Vendida", value = state.soldQuantity)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            MaterialSection(
                selectedMaterials = state.materials,
                availableMaterials = state.availableMaterials
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}