package ufsm.petsi.petservices.ui.clients.android

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import ufsm.petsi.petservices.ui.clients.pedido.ViewPedidoIntent
import ufsm.petsi.petservices.ui.clients.pedido.ViewPedidoViewModel
import ufsm.petsi.petservices.ui.components.pedido.PedidoDetailContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidoDetailSheet(
    orderId: String,
    clientName: String = "",
    onDismiss: () -> Unit
) {
    val viewModel: ViewPedidoViewModel = koinViewModel()
    val state = viewModel.state.collectAsState().value
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(orderId) {
        viewModel.handleIntent(ViewPedidoIntent.LoadPedido(orderId, clientName))
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = "Pedido",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

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