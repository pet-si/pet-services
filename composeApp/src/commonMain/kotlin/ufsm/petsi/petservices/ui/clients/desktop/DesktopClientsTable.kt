package ufsm.petsi.petservices.ui.clients.desktop

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.outlined.More_vert
import ufsm.petsi.petservices.models.Client
import ufsm.petsi.petservices.ui.components.table.ColumnDefinition
import ufsm.petsi.petservices.ui.components.table.DataTable
import ufsm.petsi.petservices.ui.components.textField.formatCpf
import ufsm.petsi.petservices.ui.components.textField.formatPhone

@Composable
fun DesktopClientsTable(
    clients: List<Client>,
    contextMenuClientId: String?,
    onOpenContextMenu: (String) -> Unit,
    onDismissContextMenu: () -> Unit,
    onClientClick: (Client) -> Unit,
    onEdit: (Client) -> Unit,
    onDelete: (Client) -> Unit,
    modifier: Modifier = Modifier
) {
    val columns = remember {
        listOf<ColumnDefinition<Client>>(
            ColumnDefinition(
                title = "Nome",
                weight = 2.5f,
                gravity = Alignment.CenterStart
            ) { client ->
                Text(
                    text = client.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            ColumnDefinition(
                title = "Telefone",
                weight = 1.5f,
                gravity = Alignment.Center
            ) { client ->
                Text(
                    text = client.phoneNumber?.let { formatPhone(it) } ?: "—",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            ColumnDefinition(
                title = "CPF",
                weight = 1.5f,
                gravity = Alignment.Center
            ) { client ->
                Text(
                    text = client.cpf?.let { formatCpf(it) } ?: "—",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            ColumnDefinition(
                title = "E-mail",
                weight = 2f,
                gravity = Alignment.Center
            ) { client ->
                Text(
                    text = client.email ?: "—",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )
    }

    DataTable(
        columns = columns,
        rows = clients,
        key = { it.idClient },
        emptyMessage = "Você ainda não possui clientes cadastrados",
        modifier = modifier,
        onRowClick = onClientClick,
        rowActions = { client ->
            IconButton(
                onClick = { onOpenContextMenu(client.idClient) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = MaterialIcons.Outlined.More_vert,
                    contentDescription = "Ações do cliente",
                    modifier = Modifier.size(20.dp)
                )
            }
            DesktopClientContextMenu(
                expanded = contextMenuClientId == client.idClient,
                onDismiss = onDismissContextMenu,
                onEdit = { onEdit(client) },
                onDelete = { onDelete(client) }
            )
        }
    )
}