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
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import ufsm.petsi.petservices.models.Material
import ufsm.petsi.petservices.ui.components.DefaultTextField

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
        FormButtons(modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, bottom = 8.dp), onCreate = {
            viewModel.handleIntent(CreateProductIntent.CreateProduct)
        }, onCancel = {
            viewModel.handleIntent(CreateProductIntent.Cancel)
        })
    }
}


@Composable
private fun ProductForm(modifier : Modifier = Modifier, onAction: (CreateProductIntent) -> Unit, state: CreateProductState) {
    Column(
        modifier = modifier
    ) {
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
            value = state.quantity.toString(), onValueChange = { text ->
                onAction(CreateProductIntent.QuantityChanged(text.filter { c -> c.isDigit() }
                    .toLongOrNull() ?: 0L))
            })

        DefaultTextField(
            label = {
                Text("Custo de Compra")
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            value = state.costPrice.toString(),
            onValueChange = {
                onAction(CreateProductIntent.CostPriceChanged(it.toDoubleOrNull() ?: 0.0))
            })

        DefaultTextField(
            label = {
                Text("Valor de Venda")
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            value = state.salePrice.toString(),
            onValueChange = {
                onAction(CreateProductIntent.SalePriceChanged(it.toDoubleOrNull() ?: 0.0))
            })

        DefaultTextField(
            label = {
                Text("Estoque Mínimo")
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            value = state.minimumStock.toString(),
            onValueChange = {
                onAction(CreateProductIntent.MinimumStockChanged(it.toIntOrNull() ?: 0))
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
            Text("Criar")
        }
    }
}