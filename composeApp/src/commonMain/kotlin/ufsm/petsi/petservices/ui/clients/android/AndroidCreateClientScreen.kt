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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import ufsm.petsi.petservices.ui.clients.create.CreateClientEffects
import ufsm.petsi.petservices.ui.clients.create.CreateClientIntent
import ufsm.petsi.petservices.ui.clients.create.CreateClientState
import ufsm.petsi.petservices.ui.clients.create.CreateClientViewModel
import ufsm.petsi.petservices.ui.components.form.ClientFormFields

@Composable
fun AndroidCreateClientScreen(
    onNavigateBack: () -> Unit,
    clientId: String? = null
) {
    val viewModel: CreateClientViewModel = koinViewModel()
    val state = viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(clientId) {
        if (clientId != null) {
            viewModel.getClient(clientId)
        }
        viewModel.effects.collect {
            when (it) {
                CreateClientEffects.NavigateBack -> onNavigateBack()
                is CreateClientEffects.ShowMessage -> {
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
            ClientForm(
                modifier = Modifier.weight(1f),
                onAction = { viewModel.handleIntent(it) },
                state = state.value
            )
            FormButtons(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                isEditMode = state.value.clientId.isNotEmpty(),
                onSave = { viewModel.handleIntent(CreateClientIntent.SaveClient) },
                onCancel = { viewModel.handleIntent(CreateClientIntent.Cancel) }
            )
        }
    }
}

@Composable
private fun ClientForm(
    modifier: Modifier = Modifier,
    onAction: (CreateClientIntent) -> Unit,
    state: CreateClientState
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Text(
            if (state.clientId.isNotEmpty()) "Editar Cliente" else "Adicionar Cliente",
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge
        )
        ClientFormFields(
            name = state.name,
            onNameChange = { onAction(CreateClientIntent.NameChanged(it)) },
            cpf = state.cpf,
            onCpfChange = { onAction(CreateClientIntent.CpfChanged(it)) },
            email = state.email,
            onEmailChange = { onAction(CreateClientIntent.EmailChanged(it)) },
            phoneNumber = state.phoneNumber,
            onPhoneChange = { onAction(CreateClientIntent.PhoneChanged(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 16.dp),
            nameError = state.nameError
        )
    }
}

@Composable
private fun FormButtons(
    modifier: Modifier = Modifier,
    isEditMode: Boolean,
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
            Text(if (isEditMode) "Salvar" else "Adicionar")
        }
    }
}