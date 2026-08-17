package ufsm.petsi.petservices.ui.products.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import ufsm.petsi.petservices.ui.components.textField.DecimalCommaVisualTransformation
import ufsm.petsi.petservices.ui.components.textField.DefaultTextField

@Composable
fun CreateProductScreen(
    onNavigateBack: () -> Unit,
    productId: String? = null
) {
    val viewModel: CreateProductViewModel = koinViewModel()
    val state = viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        if (productId != null) {
            viewModel.getProduct(productId)
        }
        viewModel.effects.collect {
            when (it) {
                CreateProductEffects.NavigateBack -> onNavigateBack()
                is CreateProductEffects.ShowMessage -> TODO()
            }
        }
    }
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxSize()
    ) {
        ProductForm(onAction = {
            viewModel.handleIntent(it)
        }, state = state.value)
        FormButtons(
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
            onCreate = {
                viewModel.handleIntent(CreateProductIntent.CreateProduct)
            },
            onCancel = {
                viewModel.handleIntent(CreateProductIntent.Cancel)
            })
    }
}


@Composable
private fun ProductForm(
    modifier: Modifier = Modifier,
    onAction: (CreateProductIntent) -> Unit,
    state: CreateProductState
) {
    Column(
        modifier = modifier
    ) {
        Text(
            "Adicionar Produto",
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )
        DefaultTextField(
            label = {
                Text("Nome do Produto")
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            value = state.name, onValueChange = {
                onAction(CreateProductIntent.NameChanged(it))
            })

        DefaultTextField(
            label = {
                Text("Quantidade")
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = state.quantity, onValueChange = { text ->
                val cleanInput = text.filter { it.isDigit() }
                onAction(CreateProductIntent.QuantityChanged(cleanInput))
            })

        DefaultTextField(
            label = {
                Text("Valor de Compra")
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            prefix = { Text("R$") },
            value = state.costPrice,
            visualTransformation = DecimalCommaVisualTransformation(),
            onValueChange = { text ->
                val cleanInput = text.filter { it.isDigit() }
                if (cleanInput.length <= 9) {
                    onAction(CreateProductIntent.CostPriceChanged(cleanInput))
                }
            })

        DefaultTextField(
            label = {
                Text("Valor de Venda")
            },
            prefix = { Text("R$") },
            visualTransformation = DecimalCommaVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            value = state.salePrice,
            onValueChange = { input ->
                val cleanInput = input.filter { it.isDigit() }
                if (cleanInput.length <= 9) {
                    onAction(CreateProductIntent.SalePriceChanged(input))
                }
            })

        DefaultTextField(
            label = {
                Text("Estoque Mínimo")
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            value = state.minimumStock,
            onValueChange = { text ->
                val cleanInput = text.filter { it.isDigit() }
                if (cleanInput.isNotBlank()) {
                    onAction(CreateProductIntent.MinimumStockChanged(cleanInput))
                }
            })


    }
}

@Composable
private fun FormButtons(modifier: Modifier = Modifier, onCreate: () -> Unit, onCancel: () -> Unit) {
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
            Text("Adicionar")
        }
    }
}