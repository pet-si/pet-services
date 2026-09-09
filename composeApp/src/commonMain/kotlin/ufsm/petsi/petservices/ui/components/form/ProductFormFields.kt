package ufsm.petsi.petservices.ui.components.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ufsm.petsi.petservices.ui.components.textField.CurrencyField
import ufsm.petsi.petservices.ui.components.textField.DefaultTextField

@Composable
fun ProductFormFields(
    name: String,
    onNameChange: (String) -> Unit,
    quantity: String,
    onQuantityChange: (String) -> Unit,
    minimumStock: String,
    onMinimumStockChange: (String) -> Unit,
    costPrice: String,
    onCostPriceChange: (String) -> Unit,
    salePrice: String,
    onSalePriceChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    nameError: String? = null,
    costPriceError: String? = null,
    salePriceError: String? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DefaultTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nome do Produto") },
            isError = nameError != null,
            errorMessage = nameError ?: ""
        )

        DefaultTextField(
            value = quantity,
            onValueChange = { input -> onQuantityChange(input.filter { c -> c.isDigit() }) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Quantidade") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        DefaultTextField(
            value = minimumStock,
            onValueChange = { input -> onMinimumStockChange(input.filter { c -> c.isDigit() }) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Estoque Mínimo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        CurrencyField(
            value = costPrice,
            onValueChange = onCostPriceChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Preço de Compra") },
            isError = costPriceError != null,
            errorMessage = costPriceError ?: ""
        )

        CurrencyField(
            value = salePrice,
            onValueChange = onSalePriceChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Preço de Venda") },
            isError = salePriceError != null,
            errorMessage = salePriceError ?: ""
        )
    }
}