package ufsm.petsi.petservices.ui.components.material

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ufsm.petsi.petservices.ui.components.textField.DecimalCommaVisualTransformation
import ufsm.petsi.petservices.ui.components.textField.DefaultTextField

@Composable
fun MaterialForm(
    name: String,
    onNameChange: (String) -> Unit,
    costPrice: String,
    onCostPriceChange: (String) -> Unit,
    minimumStock: String,
    onMinimumStockChange: (String) -> Unit,
    metric: String,
    onMetricChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    nameError: String? = null,
    costPriceError: String? = null,
    minimumStockError: String? = null,
    metricError: String? = null,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DefaultTextField(
            label = { Text("Nome do Material") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            value = name,
            onValueChange = onNameChange,
            isError = nameError != null,
            errorMessage = nameError ?: ""
        )

        DefaultTextField(
            label = { Text("Valor de Compra") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            prefix = { Text("R$") },
            visualTransformation = DecimalCommaVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = costPrice,
            onValueChange = { text ->
                val cleanInput = text.filter { it.isDigit() }
                if (cleanInput.length <= 9) {
                    onCostPriceChange(cleanInput)
                }
            },
            isError = costPriceError != null,
            errorMessage = costPriceError ?: ""
        )

        DefaultTextField(
            label = { Text("Estoque Mínimo") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = minimumStock,
            onValueChange = { text ->
                val cleanInput = text.filter { it.isDigit() }
                onMinimumStockChange(cleanInput)
            },
            isError = minimumStockError != null,
            errorMessage = minimumStockError ?: ""
        )

        DefaultTextField(
            label = { Text("Métrica") },
            placeholder = { Text("Ex: kg, g, unidade...") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            value = metric,
            onValueChange = onMetricChange,
            isError = metricError != null,
            errorMessage = metricError ?: ""
        )
    }
}
