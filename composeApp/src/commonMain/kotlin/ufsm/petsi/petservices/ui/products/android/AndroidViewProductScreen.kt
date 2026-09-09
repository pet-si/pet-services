package ufsm.petsi.petservices.ui.products.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import ufsm.petsi.petservices.models.Material
import ufsm.petsi.petservices.ui.components.material.MaterialListItem
import ufsm.petsi.petservices.ui.products.view.ViewProductEffects
import ufsm.petsi.petservices.ui.products.view.ViewProductIntent
import ufsm.petsi.petservices.ui.products.view.ViewProductViewModel
import kotlin.time.ExperimentalTime

@Composable
fun AndroidViewProductScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    productId: String
) {
    val viewModel: ViewProductViewModel = koinViewModel()
    val state = viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(productId) {
        viewModel.handleIntent(ViewProductIntent.LoadProduct(productId))
    }

    LaunchedEffect(Unit) {
        viewModel.collectEffects { effect ->
            when (effect) {
                ViewProductEffects.NavigateBack -> onNavigateBack()
                is ViewProductEffects.NavigateToEdit -> onNavigateToEdit(effect.productId)
                is ViewProductEffects.ShowMessage -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = effect.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ViewProductBody(
                productId = productId,
                onNavigateToEdit = onNavigateToEdit,
                onNavigateBack = onNavigateBack,
                isEmbedded = false,
                viewModel = viewModel
            )
        }
    }

    if (state.value.showDeleteDialog) {
        DeleteConfirmDialog(
            onConfirm = { viewModel.handleIntent(ViewProductIntent.ConfirmDeleteProduct) },
            onDismiss = { viewModel.handleIntent(ViewProductIntent.DismissDeleteDialog) }
        )
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun ViewProductBody(
    productId: String,
    onNavigateToEdit: (String) -> Unit,
    onNavigateBack: () -> Unit,
    isEmbedded: Boolean,
    viewModel: ViewProductViewModel
) {
    val state = viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = state.value.name,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )

        DetailField(label = "Quantidade em Estoque", value = state.value.quantity)
        DetailField(label = "Valor de Compra", value = "R$ ${state.value.costPrice}")
        DetailField(label = "Valor de Venda", value = "R$ ${state.value.salePrice}")
        DetailField(label = "Estoque Mínimo", value = state.value.minimumStock)
        DetailField(label = "Quantidade Vendida", value = state.value.soldQuantity)

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                "Materiais",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (state.value.materials.isEmpty()) {
                Text(
                    text = "Nenhum material associado",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                state.value.materials.forEach { productMaterial ->
                    val material = state.value.availableMaterials.find {
                        it.idMaterial == productMaterial.idMaterial
                    }
                    MaterialListItem(
                        material = material ?: Material(
                            idMaterial = productMaterial.idMaterial,
                            name = productMaterial.idMaterial,
                            costPrice = 0.0,
                            metric = ""
                        ),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.handleIntent(ViewProductIntent.DeleteProduct) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Excluir")
            }
            FilledTonalButton(
                onClick = { onNavigateToEdit(productId) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Editar")
            }
        }

        if (!isEmbedded) {
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Voltar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DeleteConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Excluir Produto") },
        text = { Text("Você tem certeza que deseja excluir este produto? Esta ação não pode ser desfeita.") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Excluir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun DetailField(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
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
