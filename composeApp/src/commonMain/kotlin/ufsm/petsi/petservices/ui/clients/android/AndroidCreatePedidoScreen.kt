package ufsm.petsi.petservices.ui.clients.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import ufsm.petsi.petservices.ui.clients.pedido.CreatePedidoEffects
import ufsm.petsi.petservices.ui.clients.pedido.CreatePedidoIntent
import ufsm.petsi.petservices.ui.clients.pedido.CreatePedidoState
import ufsm.petsi.petservices.ui.clients.pedido.CreatePedidoViewModel
import ufsm.petsi.petservices.ui.components.pedido.PedidoFormFields
import ufsm.petsi.petservices.ui.components.pedido.ProductPickerSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidCreatePedidoScreen(
    onNavigateBack: () -> Unit,
    clientId: String
) {
    val viewModel: CreatePedidoViewModel = koinViewModel()
    val state = viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.setClient(clientId)
        viewModel.effects.collect {
            when (it) {
                CreatePedidoEffects.NavigateBack -> onNavigateBack()
                is CreatePedidoEffects.ShowMessage -> {
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
            PedidoForm(
                modifier = Modifier.weight(1f),
                onAction = { viewModel.handleIntent(it) },
                state = state.value
            )
            FormButtons(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                onSave = { viewModel.handleIntent(CreatePedidoIntent.Submit) },
                onCancel = { viewModel.handleIntent(CreatePedidoIntent.Cancel) }
            )
        }
    }

    if (state.value.openProductPicker) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.handleIntent(CreatePedidoIntent.CloseProductPicker) },
            sheetState = sheetState
        ) {
            ProductPickerSection(
                products = state.value.availableProducts.filter {
                    it.name.contains(state.value.productSearchQuery, ignoreCase = true)
                },
                searchQuery = state.value.productSearchQuery,
                onSearchQueryChange = { viewModel.handleIntent(CreatePedidoIntent.ProductSearchChanged(it)) },
                onProductSelected = { viewModel.handleIntent(CreatePedidoIntent.SelectProduct(it)) },
                selectedProduct = state.value.selectedProduct,
                productQuantity = state.value.productQuantity,
                onProductQuantityChange = { viewModel.handleIntent(CreatePedidoIntent.QuantityChanged(it)) },
                onConfirm = { viewModel.handleIntent(CreatePedidoIntent.ConfirmProduct) },
                onCancel = { viewModel.handleIntent(CreatePedidoIntent.CloseProductPicker) },
                quantityError = state.value.quantityError,
                isLoading = state.value.isProductsLoading,
                errorMessage = state.value.productsErrorMessage,
                onRetry = { viewModel.handleIntent(CreatePedidoIntent.OpenProductPicker) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 40.dp)
            )
        }
    }
}

@Composable
private fun PedidoForm(
    modifier: Modifier = Modifier,
    onAction: (CreatePedidoIntent) -> Unit,
    state: CreatePedidoState
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Text(
            "Adicionar Pedido",
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )

        PedidoFormFields(
            date = state.date,
            onDateChange = { onAction(CreatePedidoIntent.DateChanged(it)) },
            products = state.products,
            availableProducts = state.availableProducts,
            onOpenProductPicker = { onAction(CreatePedidoIntent.OpenProductPicker) },
            onRemoveProduct = { onAction(CreatePedidoIntent.RemoveProduct(it)) },
            dateError = state.dateError,
            productsError = state.productsError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}

@Composable
private fun FormButtons(
    modifier: Modifier = Modifier,
    onSave: () -> Unit,
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
            onClick = onSave,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Salvar")
        }
    }
}