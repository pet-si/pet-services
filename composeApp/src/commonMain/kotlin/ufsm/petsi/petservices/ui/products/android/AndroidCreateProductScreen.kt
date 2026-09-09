package ufsm.petsi.petservices.ui.products.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.outlined.Add
import com.composables.icons.materialicons.outlined.Close
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import ufsm.petsi.petservices.models.Material
import ufsm.petsi.petservices.ui.components.material.MaterialForm
import ufsm.petsi.petservices.ui.components.material.MaterialListItem
import ufsm.petsi.petservices.ui.components.material.MaterialPicker
import ufsm.petsi.petservices.ui.components.textField.DecimalCommaVisualTransformation
import ufsm.petsi.petservices.ui.components.textField.DefaultTextField
import ufsm.petsi.petservices.ui.products.create.CreateProductEffects
import ufsm.petsi.petservices.ui.products.create.CreateProductIntent
import ufsm.petsi.petservices.ui.products.create.CreateProductState
import ufsm.petsi.petservices.ui.products.create.CreateProductViewModel
import ufsm.petsi.petservices.ui.products.create.NewMaterialField
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidCreateProductScreen(
    onNavigateBack: () -> Unit,
    onNavigateToViewProduct: (String) -> Unit = { },
    productId: String? = null
) {
    val viewModel: CreateProductViewModel = koinViewModel()
    val state = viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.loadAvailableMaterialsSync()
        if (productId != null) {
            viewModel.getProduct(productId)
        }
        viewModel.effects.collect {
            when (it) {
                CreateProductEffects.NavigateBack -> onNavigateBack()
                is CreateProductEffects.NavigateToViewProduct -> onNavigateBack()
                is CreateProductEffects.ShowMessage -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = it.message,
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
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            ProductForm(
                modifier = Modifier.weight(1f),
                onAction = { viewModel.handleIntent(it) },
                state = state.value
            )
            FormButtons(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                isEditMode = state.value.productId.isNotEmpty(),
                onCreate = { viewModel.handleIntent(CreateProductIntent.CreateProduct) },
                onCancel = { viewModel.handleIntent(CreateProductIntent.Cancel) }
            )
        }
    }

    if (state.value.openMaterialEditor) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.handleIntent(CreateProductIntent.CloseMaterialEditor) },
            sheetState = sheetState
        ) {
            MaterialEditorContent(
                state = state.value,
                onAction = { viewModel.handleIntent(it) }
            )
        }
    }
}

@Composable
private fun MaterialEditorContent(
    state: CreateProductState,
    onAction: (CreateProductIntent) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        if (state.showNewMaterialForm) {
            Text(
                "Novo Material",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            MaterialForm(
                name = state.newMaterialName,
                onNameChange = { onAction(CreateProductIntent.NewMaterialFieldChanged(NewMaterialField.NAME, it)) },
                costPrice = state.newMaterialCostPrice,
                onCostPriceChange = { onAction(CreateProductIntent.NewMaterialFieldChanged(NewMaterialField.COST_PRICE, it)) },
                minimumStock = state.newMaterialMinimumStock,
                onMinimumStockChange = { onAction(CreateProductIntent.NewMaterialFieldChanged(NewMaterialField.MINIMUM_STOCK, it)) },
                metric = state.newMaterialMetric,
                onMetricChange = { onAction(CreateProductIntent.NewMaterialFieldChanged(NewMaterialField.METRIC, it)) },
                nameError = state.newMaterialNameError,
                costPriceError = state.newMaterialCostPriceError,
                minimumStockError = state.newMaterialMinimumStockError,
                metricError = state.newMaterialMetricError
            )
            FilledTonalButton(
                onClick = { onAction(CreateProductIntent.CreateAndSelectMaterial) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Criar e Selecionar")
            }
        } else {
            MaterialPicker(
                materials = state.availableMaterials.filter {
                    it.name.contains(state.materialSearchQuery, ignoreCase = true)
                },
                searchQuery = state.materialSearchQuery,
                onSearchQueryChange = { onAction(CreateProductIntent.MaterialSearchChanged(it)) },
                onMaterialSelected = { onAction(CreateProductIntent.SelectMaterial(it)) },
                selectedMaterialId = state.selectedMaterial?.idMaterial
            )

            state.selectedMaterial?.let { material ->
                Text(
                    text = "Selecionado: ${material.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                DefaultTextField(
                    label = { Text("Quantidade") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    value = state.materialQuantity,
                    onValueChange = { onAction(CreateProductIntent.MaterialQuantityChanged(it)) }
                )
                Button(
                    onClick = { onAction(CreateProductIntent.ConfirmMaterial) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirmar")
                }
            }
        }

        FilledTonalButton(
            onClick = { onAction(CreateProductIntent.ToggleNewMaterialForm) },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                if (state.showNewMaterialForm) "Buscar Material Existente"
                else "Criar Novo Material"
            )
        }
    }
}


@Composable
private fun ProductForm(
    modifier: Modifier = Modifier,
    onAction: (CreateProductIntent) -> Unit,
    state: CreateProductState
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Text(
            if (state.productId.isNotEmpty()) "Editar Produto" else "Adicionar Produto",
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )
        DefaultTextField(
            label = { Text("Nome do Produto") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            value = state.name,
            onValueChange = { onAction(CreateProductIntent.NameChanged(it)) },
            isError = state.nameError != null,
            errorMessage = state.nameError ?: ""
        )

        DefaultTextField(
            label = { Text("Quantidade") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = state.quantity,
            onValueChange = { text ->
                val cleanInput = text.filter { it.isDigit() }
                onAction(CreateProductIntent.QuantityChanged(cleanInput))
            },
            isError = state.quantityError != null,
            errorMessage = state.quantityError ?: ""
        )

        DefaultTextField(
            label = { Text("Valor de Compra") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            prefix = { Text("R$") },
            value = state.costPrice,
            visualTransformation = DecimalCommaVisualTransformation(),
            onValueChange = { text ->
                val cleanInput = text.filter { it.isDigit() }
                if (cleanInput.length <= 9) {
                    onAction(CreateProductIntent.CostPriceChanged(cleanInput))
                }
            },
            isError = state.costPriceError != null,
            errorMessage = state.costPriceError ?: ""
        )

        DefaultTextField(
            label = { Text("Valor de Venda") },
            prefix = { Text("R$") },
            visualTransformation = DecimalCommaVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            value = state.salePrice,
            onValueChange = { input ->
                val cleanInput = input.filter { it.isDigit() }
                if (cleanInput.length <= 9) {
                    onAction(CreateProductIntent.SalePriceChanged(cleanInput))
                }
            },
            isError = state.salePriceError != null,
            errorMessage = state.salePriceError ?: ""
        )

        DefaultTextField(
            label = { Text("Estoque Mínimo") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            value = state.minimumStock,
            onValueChange = { text ->
                val cleanInput = text.filter { it.isDigit() }
                onAction(CreateProductIntent.MinimumStockChanged(cleanInput))
            },
            isError = state.minimumStockError != null,
            errorMessage = state.minimumStockError ?: ""
        )

        MaterialsSection(
            state = state,
            onAction = onAction
        )
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun MaterialsSection(
    state: CreateProductState,
    onAction: (CreateProductIntent) -> Unit
) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Materiais",
                style = MaterialTheme.typography.titleMedium
            )
            FilledTonalButton(
                onClick = { onAction(CreateProductIntent.OpenMaterialEditor) },
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = MaterialIcons.Outlined.Add,
                        contentDescription = "Adicionar Material"
                    )
                    Text("Adicionar")
                }
            }
        }

        if (state.materials.isEmpty()) {
            Text(
                text = "Nenhum material associado",
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            state.materials.forEach { productMaterial ->
                val material =
                    state.availableMaterials.find { it.idMaterial == productMaterial.idMaterial }
                MaterialListItem(
                    material = material ?: Material(
                        idMaterial = productMaterial.idMaterial,
                        name = productMaterial.idMaterial,
                        costPrice = 0.0,
                        metric = ""
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    trailing = {
                        FilledTonalButton(
                            modifier = Modifier.padding(start = 8.dp),
                            onClick = { onAction(CreateProductIntent.RemoveMaterial(productMaterial.idMaterial)) },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = MaterialIcons.Outlined.Close,
                                contentDescription = "Remover Material"
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun FormButtons(
    modifier: Modifier = Modifier,
    isEditMode: Boolean,
    onCreate: () -> Unit,
    onCancel: () -> Unit
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = onCancel,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Cancelar")
        }
        Button(
            onClick = onCreate,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(if (isEditMode) "Salvar" else "Adicionar")
        }
    }
}
