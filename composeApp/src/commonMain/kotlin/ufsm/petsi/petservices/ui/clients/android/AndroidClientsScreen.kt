package ufsm.petsi.petservices.ui.clients.android

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.outlined.Add
import com.composables.icons.materialicons.outlined.Check
import com.composables.icons.materialicons.outlined.Delete
import com.composables.icons.materialicons.outlined.Edit
import org.koin.compose.viewmodel.koinViewModel
import ufsm.petsi.petservices.models.Client
import ufsm.petsi.petservices.ui.clients.ClientsEffects
import ufsm.petsi.petservices.ui.clients.ClientsIntent
import ufsm.petsi.petservices.ui.clients.ClientsViewModel
import ufsm.petsi.petservices.ui.components.client.ClientListItem

@Composable
fun AndroidClientsScreen(
    onCreateClient: () -> Unit,
    onClientSelected: (String) -> Unit,
    onClientEdit: (String) -> Unit,
    onAddPedido: (String) -> Unit = {}
) {
    val viewModel: ClientsViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsState().value

    var viewingPedidoOrderId by remember { mutableStateOf<String?>(null) }
    var viewingPedidoClientName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effects ->
            when (effects) {
                is ClientsEffects.NavigateToClientCreation -> onCreateClient()
                is ClientsEffects.NavigateToClientDetails -> onClientSelected(effects.clientId)
                is ClientsEffects.NavigateToClientEdit -> onClientEdit(effects.clientId)
            }
        }
    }

    AnimatedContent(
        targetState = uiState.clients.isNotEmpty(),
        modifier = Modifier.fillMaxSize()
    ) { hasClients ->
        Box(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            if (hasClients) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.clients,
                        key = { it.idClient }
                    ) { client ->
                        val dismissState = rememberSwipeToDismissBoxState()

                        LaunchedEffect(dismissState.currentValue) {
                            when (dismissState.currentValue) {
                                SwipeToDismissBoxValue.EndToStart -> {
                                    viewModel.handleIntent(ClientsIntent.OnClientDelete(client.idClient))
                                    dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                                }
                                SwipeToDismissBoxValue.StartToEnd -> {
                                    viewModel.handleIntent(ClientsIntent.OnClientEdit(client.idClient))
                                    dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                                }
                                SwipeToDismissBoxValue.Settled -> {}
                            }
                        }

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                SwipeBackground(dismissState = dismissState)
                            },
                            enableDismissFromStartToEnd = true,
                            enableDismissFromEndToStart = true,
                            modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        ) {
                            ClientListItem(
                                client = client,
                                onClick = {
                                    viewModel.handleIntent(ClientsIntent.OnClientClick(client.idClient))
                                },
                                expanded = uiState.expandedClientId == client.idClient
                            ) {
                                ClientExpandedContent(
                                    client = client,
                                    pedidos = uiState.pedidosByClient[client.idClient].orEmpty(),
                                    productsByOrder = uiState.purchaseOrderProductsByOrder,
                                    productsById = uiState.productsById,
                                    onViewClient = {
                                        viewModel.handleIntent(ClientsIntent.OnClientView(client.idClient))
                                    },
                                    onEditClient = {
                                        viewModel.handleIntent(ClientsIntent.OnClientEdit(client.idClient))
                                    },
                                    onAddPedido = {
                                        onAddPedido(client.idClient)
                                    },
                                    onPedidoClick = { orderId ->
                                        viewingPedidoOrderId = orderId
                                        viewingPedidoClientName = client.name
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "Você ainda não possui clientes cadastrados",
                    modifier = Modifier.padding(top = 32.dp)
                )
            }
            FloatingActionButton(
                onClick = {
                    viewModel.handleIntent(ClientsIntent.OnAddClientClick)
                },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
            ) {
                Icon(
                    imageVector = MaterialIcons.Outlined.Add,
                    contentDescription = "Adicionar Cliente"
                )
            }
        }
    }

    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.handleIntent(ClientsIntent.DismissDeleteDialog) },
            title = { Text("Excluir Cliente") },
            text = {
                Text(
                    "Você tem certeza que deseja excluir o cliente" +
                            uiState.clientToDelete?.let { " \"${it.name}\"" }.orEmpty() +
                            "? Esta ação não pode ser desfeita."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.handleIntent(ClientsIntent.ConfirmDeleteClient) }
                ) {
                    Text("Excluir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.handleIntent(ClientsIntent.DismissDeleteDialog) }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    viewingPedidoOrderId?.let { orderId ->
        PedidoDetailSheet(
            orderId = orderId,
            clientName = viewingPedidoClientName,
            onDismiss = {
                viewingPedidoOrderId = null
                viewingPedidoClientName = ""
            }
        )
    }
}

@Composable
private fun ClientExpandedContent(
    client: Client,
    pedidos: List<ufsm.petsi.petservices.models.PurchaseOrder>,
    productsByOrder: Map<String, List<ufsm.petsi.petservices.models.PurchaseOrderProduct>>,
    productsById: Map<String, ufsm.petsi.petservices.models.Product>,
    onViewClient: () -> Unit,
    onEditClient: () -> Unit,
    onAddPedido: () -> Unit,
    onPedidoClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Pedidos",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (pedidos.isEmpty()) {
            Text(
                text = "Nenhum pedido cadastrado",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        } else {
            pedidos.forEach { pedido ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { onPedidoClick(pedido.idPurchaseOrder) })
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = MaterialIcons.Outlined.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = pedido.date,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    val productNames = productsByOrder[pedido.idPurchaseOrder].orEmpty()
                        .mapNotNull { id -> productsById[id.idProduct]?.name }
                        .take(3)
                    if (productNames.isNotEmpty()) {
                        Text(
                            text = productNames.joinToString(" · "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 26.dp, top = 2.dp)
                        )
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onAddPedido) {
                Text("Adicionar Pedido")
            }
            TextButton(onClick = onViewClient) {
                Text("Visualizar")
            }
            TextButton(onClick = onEditClient) {
                Text("Editar")
            }
        }
    }
}

@Composable
private fun SwipeBackground(dismissState: SwipeToDismissBoxState) {
    val direction = dismissState.dismissDirection

    val color = when (direction) {
        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
        SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }

    val icon = when (direction) {
        SwipeToDismissBoxValue.EndToStart -> MaterialIcons.Outlined.Delete
        SwipeToDismissBoxValue.StartToEnd -> MaterialIcons.Outlined.Edit
        else -> MaterialIcons.Outlined.Delete
    }

    val alignment = when (direction) {
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        else -> Alignment.Center
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp),
        contentAlignment = alignment
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}