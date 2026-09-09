package ufsm.petsi.petservices.ui.clients.desktop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.outlined.Add
import com.composables.icons.materialicons.outlined.Search
import org.koin.compose.viewmodel.koinViewModel
import ufsm.petsi.petservices.ui.clients.ClientsIntent
import ufsm.petsi.petservices.ui.clients.ClientsViewModel
import ufsm.petsi.petservices.ui.components.dialog.ModalPopup
import ufsm.petsi.petservices.ui.components.textField.DefaultTextField

@Composable
fun DesktopClientsScreen(modifier: Modifier = Modifier) {
    val viewModel: ClientsViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsState().value

    var searchQuery by remember { mutableStateOf("") }
    var contextMenuClientId by remember { mutableStateOf<String?>(null) }
    var editingClientId by remember { mutableStateOf<String?>(null) }
    var showCreatePopup by remember { mutableStateOf(false) }
    var viewingClientId by remember { mutableStateOf<String?>(null) }
    var creatingPedidoClientId by remember { mutableStateOf<String?>(null) }
    var showClientSelect by remember { mutableStateOf(false) }
    var viewingPedidoOrderId by remember { mutableStateOf<String?>(null) }
    var viewingPedidoClientName by remember { mutableStateOf("") }

    val filteredClients = remember(searchQuery, uiState.clients) {
        if (searchQuery.isBlank()) uiState.clients
        else uiState.clients.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    (it.cpf ?: "").contains(searchQuery, ignoreCase = true) ||
                    (it.email ?: "").contains(searchQuery, ignoreCase = true) ||
                    (it.phoneNumber ?: "").contains(searchQuery, ignoreCase = true)
        }
    }

    val pendingPedidos = remember(uiState.pedidosByClient) {
        uiState.pedidosByClient.values.flatten()
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp, vertical = 24.dp)
        ) {
            ScreenHeader()

            DesktopPendingPedidosSection(
                clients = uiState.clients,
                pedidos = pendingPedidos,
                productsByOrder = uiState.purchaseOrderProductsByOrder,
                productsById = uiState.productsById,
                onAddPedido = { showClientSelect = true },
                onPedidoClick = { orderId, clientName ->
                    viewingPedidoOrderId = orderId
                    viewingPedidoClientName = clientName
                },
                modifier = Modifier.padding(top = 24.dp)
            )

            SearchAndActionsBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onCreateClick = {
                    editingClientId = null
                    showCreatePopup = true
                },
                modifier = Modifier.padding(top = 24.dp)
            )

            DesktopClientsTable(
                clients = filteredClients,
                contextMenuClientId = contextMenuClientId,
                onOpenContextMenu = { contextMenuClientId = it },
                onDismissContextMenu = { contextMenuClientId = null },
                onClientClick = { viewingClientId = it.idClient },
                onEdit = { client ->
                    editingClientId = client.idClient
                    showCreatePopup = true
                },
                onDelete = { client ->
                    viewModel.handleIntent(ClientsIntent.OnClientDelete(client.idClient))
                },
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }

    if (showCreatePopup) {
        DesktopCreateClientPopup(
            clientId = editingClientId,
            onDismiss = { showCreatePopup = false }
        )
    }

    viewingClientId?.let { clientId ->
        DesktopClientViewPopup(
            clientId = clientId,
            onDismiss = { viewingClientId = null },
            onEdit = { id ->
                viewingClientId = null
                editingClientId = id
                showCreatePopup = true
            },
            onAddPedido = { id ->
                viewingClientId = null
                creatingPedidoClientId = id
            },
            onPedidoClick = { orderId, clientName ->
                viewingPedidoOrderId = orderId
                viewingPedidoClientName = clientName
            }
        )
    }

    creatingPedidoClientId?.let { clientId ->
        DesktopCreatePedidoPopup(
            clientId = clientId,
            onDismiss = { creatingPedidoClientId = null }
        )
    }

    if (showClientSelect) {
        ClientSelectPopup(
            clients = uiState.clients,
            onDismiss = { showClientSelect = false },
            onClientSelected = { clientId ->
                showClientSelect = false
                creatingPedidoClientId = clientId
            }
        )
    }

    if (uiState.showDeleteDialog) {
        ModalPopup(
            onDismissRequest = { viewModel.handleIntent(ClientsIntent.DismissDeleteDialog) },
            title = {
                Text(
                    text = "Excluir Cliente",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            buttons = {
                TextButton(
                    onClick = { viewModel.handleIntent(ClientsIntent.DismissDeleteDialog) }
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = { viewModel.handleIntent(ClientsIntent.ConfirmDeleteClient) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Excluir")
                }
            }
        ) {
            Text(
                text = "Você tem certeza que deseja excluir o cliente" +
                    uiState.clientToDelete?.let { " \"${it.name}\"" }.orEmpty() +
                    "? Esta ação não pode ser desfeita.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    viewingPedidoOrderId?.let { orderId ->
        DesktopPedidoDetailPopup(
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
private fun ClientSelectPopup(
    clients: List<ufsm.petsi.petservices.models.Client>,
    onDismiss: () -> Unit,
    onClientSelected: (String) -> Unit
) {
    ModalPopup(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Selecionar Cliente",
                style = MaterialTheme.typography.titleLarge
            )
        },
        buttons = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    ) {
        if (clients.isEmpty()) {
            Text(
                text = "Nenhum cliente cadastrado",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                clients.forEach { client ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onClientSelected(client.idClient) },
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = client.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScreenHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Exemplo & Cia Ltda.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Gerenciamento de clientes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SearchAndActionsBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DefaultTextField(
            value = searchQuery,
            modifier = Modifier.weight(1f).height(64.dp),
            onValueChange = onSearchQueryChange,
            label = { Text("Buscar cliente...") },
            leadingIcon = {
                Icon(
                    imageVector = MaterialIcons.Outlined.Search,
                    contentDescription = null
                )
            }
        )
        Button(
            onClick = onCreateClick,
            modifier = Modifier.padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MaterialIcons.Outlined.Add,
                    contentDescription = null
                )
                Text("Criar")
            }
        }
    }
}