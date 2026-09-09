package ufsm.petsi.petservices.ui.components.textField

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CurrencyField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    DefaultTextField(
        value = value,
        onValueChange = { input ->
            val clean = input.filter { c -> c.isDigit() }
            if (clean.length <= 9) onValueChange(clean)
        },
        modifier = modifier,
        label = label,
        prefix = { Text("R$") },
        visualTransformation = DecimalCommaVisualTransformation(),
        isError = isError,
        errorMessage = errorMessage
    )
}