package ufsm.petsi.petservices.ui.clients.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.outlined.Chevron_left
import com.composables.icons.materialicons.outlined.Chevron_right
import kotlinx.coroutines.launch
import ufsm.petsi.petservices.models.Client
import ufsm.petsi.petservices.models.Product
import ufsm.petsi.petservices.models.PurchaseOrder
import ufsm.petsi.petservices.models.PurchaseOrderProduct
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun DesktopPendingPedidosSection(
    clients: List<Client>,
    pedidos: List<PurchaseOrder>,
    productsByOrder: Map<String, List<PurchaseOrderProduct>>,
    productsById: Map<String, Product>,
    onAddPedido: () -> Unit = {},
    onPedidoClick: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val sortedPedidos = pedidos.sortedByDescending { it.updatedAt }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pedidos Recentes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (sortedPedidos.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                    ) {
                        Text(
                            text = sortedPedidos.size.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val target = (listState.firstVisibleItemIndex - 1).coerceAtLeast(0)
                        scope.launch { listState.animateScrollToItem(target) }
                    },
                    enabled = listState.canScrollBackward
                ) {
                    Icon(
                        imageVector = MaterialIcons.Outlined.Chevron_left,
                        contentDescription = "Pedidos anteriores"
                    )
                }
                IconButton(
                    onClick = {
                        val target = listState.firstVisibleItemIndex + 1
                        scope.launch { listState.animateScrollToItem(target) }
                    },
                    enabled = listState.canScrollForward
                ) {
                    Icon(
                        imageVector = MaterialIcons.Outlined.Chevron_right,
                        contentDescription = "Próximos pedidos"
                    )
                }
                FilledTonalButton(
                    onClick = onAddPedido,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Adicionar Pedido")
                }
            }
        }

        if (sortedPedidos.isEmpty()) {
            Text(
                text = "Nenhum pedido recente",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp)
            )
        } else {
            LazyRow(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                if (event.type == PointerEventType.Scroll) {
                                    val delta = event.changes.firstOrNull()
                                        ?.scrollDelta
                                        ?: androidx.compose.ui.geometry.Offset.Zero
                                    val amount = if (delta.x != 0f) delta.x else delta.y
                                    if (amount != 0f) {
                                        listState.dispatchRawDelta(amount * 20)
                                    }
                                }
                            }
                        }
                    },
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                items(sortedPedidos) { pedido ->
                    val clientName = clients.firstOrNull { it.idClient == pedido.idClient }?.name
                        ?: pedido.idClient
                    val productNames = productsByOrder[pedido.idPurchaseOrder].orEmpty()
                        .mapNotNull { line -> productsById[line.idProduct]?.name }
                        .take(3)
                    PedidoCard(
                        clientName = clientName,
                        date = pedido.date,
                        productNames = productNames,
                        onClick = { onPedidoClick(pedido.idPurchaseOrder, clientName) },
                        modifier = Modifier.width(280.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PedidoCard(
    clientName: String,
    date: String,
    productNames: List<String>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = clientName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            if (productNames.isNotEmpty()) {
                Text(
                    text = productNames.joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ver pedido",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = MaterialIcons.Outlined.Chevron_right,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 2.dp)
                )
            }
        }
    }
}