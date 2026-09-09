package ufsm.petsi.petservices.ui.clients.desktop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.outlined.Check
import org.koin.compose.viewmodel.koinViewModel
import ufsm.petsi.petservices.ui.clients.view.ViewClientIntent
import ufsm.petsi.petservices.ui.clients.view.ViewClientViewModel
import ufsm.petsi.petservices.ui.components.dialog.ModalPopup
import ufsm.petsi.petservices.ui.components.textField.formatCpf
import ufsm.petsi.petservices.ui.components.textField.formatPhone

@Composable
fun DesktopClientViewPopup(
    clientId: String,
    onDismiss: () -> Unit,
    onEdit: (String) -> Unit,
    onAddPedido: (String) -> Unit = {},
    onPedidoClick: (String, String) -> Unit = { _, _ -> }
) {
    val viewModel: ViewClientViewModel = koinViewModel()
    val state = viewModel.state.collectAsState().value

    LaunchedEffect(clientId) {
        viewModel.handleIntent(ViewClientIntent.LoadClient(clientId))
    }

    ModalPopup(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = state.name.ifEmpty { "Cliente" },
                style = MaterialTheme.typography.titleLarge
            )
        },
        buttons = {
            TextButton(onClick = onDismiss) { Text("Fechar") }
            Button(
                onClick = { onEdit(state.clientId) },
                enabled = state.clientId.isNotEmpty(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Editar")
            }
        }
    ) {
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Carregando cliente...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else if (state.errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.errorMessage.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            DetailRow(label = "CPF", value = state.cpf.orEmpty().let { if (it.isBlank()) "—" else formatCpf(it) })
            DetailRow(label = "E-mail", value = state.email.orEmpty().ifBlank { "—" })
            DetailRow(label = "Telefone", value = state.phoneNumber.orEmpty().let { if (it.isBlank()) "—" else formatPhone(it) })
            DetailRow(label = "Pontos", value = state.points)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pedidos",
                    style = MaterialTheme.typography.titleMedium
                )
                androidx.compose.material3.FilledTonalButton(
                    onClick = { onAddPedido(state.clientId) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Adicionar Pedido")
                }
            }
            Column(modifier = Modifier.padding(top = 8.dp)) {
                if (state.pedidos.isEmpty()) {
                    Text(
                        text = "Nenhum pedido cadastrado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    state.pedidos.forEach { pedido ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPedidoClick(pedido.idPurchaseOrder, state.name) }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = pedido.date,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            androidx.compose.material3.Icon(
                                imageVector = MaterialIcons.Outlined.Check,
                                contentDescription = "Ver pedido",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
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