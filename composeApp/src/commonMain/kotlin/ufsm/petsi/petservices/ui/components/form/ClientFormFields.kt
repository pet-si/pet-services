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
import ufsm.petsi.petservices.ui.components.textField.CpfVisualTransformation
import ufsm.petsi.petservices.ui.components.textField.DefaultTextField
import ufsm.petsi.petservices.ui.components.textField.PhoneVisualTransformation

@Composable
fun ClientFormFields(
    name: String,
    onNameChange: (String) -> Unit,
    cpf: String,
    onCpfChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phoneNumber: String,
    onPhoneChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    nameError: String? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DefaultTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nome do Cliente") },
            isError = nameError != null,
            errorMessage = nameError ?: ""
        )

        DefaultTextField(
            value = cpf,
            onValueChange = { input ->
                val clean = input.filter { c -> c.isDigit() }.take(11)
                onCpfChange(clean)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("CPF") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = CpfVisualTransformation()
        )

        DefaultTextField(
            value = phoneNumber,
            onValueChange = { input ->
                val clean = input.filter { c -> c.isDigit() }.take(11)
                onPhoneChange(clean)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Telefone") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            visualTransformation = PhoneVisualTransformation()
        )

        DefaultTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("E-mail") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
    }
}