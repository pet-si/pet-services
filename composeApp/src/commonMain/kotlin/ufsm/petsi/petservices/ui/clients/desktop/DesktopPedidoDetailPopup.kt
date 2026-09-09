package ufsm.petsi.petservices.ui.clients.desktop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import ufsm.petsi.petservices.ui.clients.pedido.ViewPedidoIntent
import ufsm.petsi.petservices.ui.clients.pedido.ViewPedidoViewModel
import ufsm.petsi.petservices.ui.components.dialog.ModalPopup
import ufsm.petsi.petservices.ui.components.pedido.PedidoDetailContent

@Composable
fun DesktopPedidoDetailPopup(
    orderId: String,
    clientName: String = "",
    onDismiss: () -> Unit
) {
    val viewModel: ViewPedidoViewModel = koinViewModel()
    val state = viewModel.state.collectAsState().value

    LaunchedEffect(orderId) {
        viewModel.handleIntent(ViewPedidoIntent.LoadPedido(orderId, clientName))
    }

    ModalPopup(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Pedido",
                style = MaterialTheme.typography.titleLarge
            )
        },
        buttons = {
            TextButton(onClick = onDismiss) { Text("Fechar") }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (state.isLoading) {
                Text(
                    text = "Carregando pedido...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                PedidoDetailContent(
                    date = state.date,
                    clientName = state.clientName,
                    products = state.products,
                    availableProducts = state.availableProducts
                )
            }
        }
    }
}