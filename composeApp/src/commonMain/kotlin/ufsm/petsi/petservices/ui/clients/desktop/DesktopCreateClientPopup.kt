package ufsm.petsi.petservices.ui.clients.desktop

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
import ufsm.petsi.petservices.ui.clients.create.CreateClientEffects
import ufsm.petsi.petservices.ui.clients.create.CreateClientIntent
import ufsm.petsi.petservices.ui.clients.create.CreateClientViewModel
import ufsm.petsi.petservices.ui.components.dialog.ModalPopup
import ufsm.petsi.petservices.ui.components.form.ClientFormFields

@Composable
fun DesktopCreateClientPopup(
    clientId: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: CreateClientViewModel = koinViewModel()
    val state = viewModel.state.collectAsState().value

    LaunchedEffect(clientId) {
        if (clientId != null) {
            viewModel.getClient(clientId)
        } else {
            viewModel.resetForCreate()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                CreateClientEffects.NavigateBack -> onDismiss()
                is CreateClientEffects.ShowMessage -> Unit
            }
        }
    }

    val isEdit = clientId != null

    ModalPopup(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEdit) "Editar Cliente" else "Criar Cliente",
                style = MaterialTheme.typography.titleLarge
            )
        },
        buttons = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
            Button(
                onClick = { viewModel.handleIntent(CreateClientIntent.SaveClient) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(if (isEdit) "Salvar" else "Criar")
            }
        },
        modifier = modifier
    ) {
        ClientFormFields(
            name = state.name,
            onNameChange = { viewModel.handleIntent(CreateClientIntent.NameChanged(it)) },
            cpf = state.cpf,
            onCpfChange = { viewModel.handleIntent(CreateClientIntent.CpfChanged(it)) },
            email = state.email,
            onEmailChange = { viewModel.handleIntent(CreateClientIntent.EmailChanged(it)) },
            phoneNumber = state.phoneNumber,
            onPhoneChange = { viewModel.handleIntent(CreateClientIntent.PhoneChanged(it)) },
            nameError = state.nameError
        )
    }
}