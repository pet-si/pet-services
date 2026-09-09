package ufsm.petsi.petservices.ui.clients.desktop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import ufsm.petsi.petservices.ui.clients.pedido.CreatePedidoEffects
import ufsm.petsi.petservices.ui.clients.pedido.CreatePedidoIntent
import ufsm.petsi.petservices.ui.clients.pedido.CreatePedidoViewModel
import ufsm.petsi.petservices.ui.components.dialog.ModalPopup
import ufsm.petsi.petservices.ui.components.pedido.PedidoFormFields
import ufsm.petsi.petservices.ui.components.pedido.ProductPickerSection

@Composable
fun DesktopCreatePedidoPopup(
    clientId: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: CreatePedidoViewModel = koinViewModel()
    val state = viewModel.state.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.setClient(clientId)
        viewModel.effects.collect { effect ->
            when (effect) {
                CreatePedidoEffects.NavigateBack -> onDismiss()
                is CreatePedidoEffects.ShowMessage -> Unit
            }
        }
    }

    ModalPopup(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Adicionar Pedido",
                style = MaterialTheme.typography.titleLarge
            )
        },
        buttons = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
            Button(
                onClick = { viewModel.handleIntent(CreatePedidoIntent.Submit) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Salvar")
            }
        },
        modifier = modifier
    ) {
        PedidoFormFields(
            date = state.date,
            onDateChange = { viewModel.handleIntent(CreatePedidoIntent.DateChanged(it)) },
            products = state.products,
            availableProducts = state.availableProducts,
            onOpenProductPicker = { viewModel.handleIntent(CreatePedidoIntent.OpenProductPicker) },
            onRemoveProduct = { viewModel.handleIntent(CreatePedidoIntent.RemoveProduct(it)) },
            dateError = state.dateError,
            productsError = state.productsError
        )

        if (state.openProductPicker) {
            ProductPickerSection(
                products = state.availableProducts.filter {
                    it.name.contains(state.productSearchQuery, ignoreCase = true)
                },
                searchQuery = state.productSearchQuery,
                onSearchQueryChange = { viewModel.handleIntent(CreatePedidoIntent.ProductSearchChanged(it)) },
                onProductSelected = { viewModel.handleIntent(CreatePedidoIntent.SelectProduct(it)) },
                selectedProduct = state.selectedProduct,
                productQuantity = state.productQuantity,
                onProductQuantityChange = { viewModel.handleIntent(CreatePedidoIntent.QuantityChanged(it)) },
                onConfirm = { viewModel.handleIntent(CreatePedidoIntent.ConfirmProduct) },
                onCancel = { viewModel.handleIntent(CreatePedidoIntent.CloseProductPicker) },
                quantityError = state.quantityError,
                isLoading = state.isProductsLoading,
                errorMessage = state.productsErrorMessage,
                onRetry = { viewModel.handleIntent(CreatePedidoIntent.OpenProductPicker) },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}