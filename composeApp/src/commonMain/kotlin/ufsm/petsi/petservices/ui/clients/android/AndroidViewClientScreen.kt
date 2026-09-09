package ufsm.petsi.petservices.ui.clients.android

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.outlined.Check
import org.koin.compose.viewmodel.koinViewModel
import ufsm.petsi.petservices.ui.clients.view.ViewClientEffects
import ufsm.petsi.petservices.ui.clients.view.ViewClientIntent
import ufsm.petsi.petservices.ui.clients.view.ViewClientViewModel
import ufsm.petsi.petservices.ui.components.textField.formatCpf
import ufsm.petsi.petservices.ui.components.textField.formatPhone
import kotlin.collections.mapNotNull
import kotlin.collections.orEmpty

@Composable
fun AndroidViewClientScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    clientId: String,
    onAddPedido: (String) -> Unit = {}
) {
    val viewModel: ViewClientViewModel = koinViewModel()
    val state = viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var viewingPedidoOrderId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(clientId) {
        viewModel.handleIntent(ViewClientIntent.LoadClient(clientId))
    }

    LaunchedEffect(Unit) {
        viewModel.collectEffects { effect ->
            when (effect) {
                ViewClientEffects.NavigateBack -> onNavigateBack()
                is ViewClientEffects.NavigateToEdit -> onNavigateToEdit(effect.clientId)
                is ViewClientEffects.ShowMessage -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = effect.message,
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = state.value.name,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )

            DetailField(label = "CPF", value = state.value.cpf.orEmpty().let { if (it.isBlank()) "—" else formatCpf(it) })
            DetailField(label = "E-mail", value = state.value.email.orEmpty().ifBlank { "—" })
            DetailField(label = "Telefone", value = state.value.phoneNumber.orEmpty().let { if (it.isBlank()) "—" else formatPhone(it) })
            DetailField(label = "Pontos", value = state.value.points)

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text(
                        "Pedidos",
                        style = MaterialTheme.typography.titleMedium
                    )
                    FilledTonalButton(
                        onClick = { onAddPedido(clientId) },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Adicionar Pedido")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (state.value.pedidos.isEmpty()) {
                    Text(
                        text = "Nenhum pedido cadastrado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    state.value.pedidos.forEach { pedido ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewingPedidoOrderId = pedido.idPurchaseOrder }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text(
                                text = pedido.date,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Icon(
                                imageVector = MaterialIcons.Outlined.Check,
                                contentDescription = "Ver pedido",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            ) {
                OutlinedButton(
                    onClick = { viewModel.handleIntent(ViewClientIntent.DeleteClient) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Excluir")
                }
                FilledTonalButton(
                    onClick = { onNavigateToEdit(clientId) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Editar")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Voltar")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (state.value.showDeleteDialog) {
        DeleteConfirmDialog(
            onConfirm = { viewModel.handleIntent(ViewClientIntent.ConfirmDeleteClient) },
            onDismiss = { viewModel.handleIntent(ViewClientIntent.DismissDeleteDialog) }
        )
    }

    viewingPedidoOrderId?.let { orderId ->
        PedidoDetailSheet(
            orderId = orderId,
            clientName = state.value.name,
            onDismiss = { viewingPedidoOrderId = null }
        )
    }
}

@Composable
private fun DeleteConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Excluir Cliente") },
        text = { Text("Você tem certeza que deseja excluir este cliente? Esta ação não pode ser desfeita.") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Excluir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun DetailField(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}