package ufsm.petsi.petservices.ui.products.desktop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import ufsm.petsi.petservices.models.Product
import ufsm.petsi.petservices.ui.components.table.ColumnDefinition
import ufsm.petsi.petservices.ui.components.table.DataTable

@Composable
fun DesktopProductTable(
    products: List<Product>,
    contextMenuProductId: String?,
    onOpenContextMenu: (String) -> Unit,
    onDismissContextMenu: () -> Unit,
    onProductClick: (Product) -> Unit,
    onEdit: (Product) -> Unit,
    onDuplicate: (Product) -> Unit,
    onDelete: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val columns = remember {
        listOf<ColumnDefinition<Product>>(
            ColumnDefinition(
                title = "Produtos | Materiais",
                weight = 3f,
                gravity = Alignment.CenterStart
            ) { product ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (product.materials.isEmpty()) "Sem materiais" else "${product.materials.size} materiais",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            },
            ColumnDefinition(
                title = "Quantidade",
                weight = 1f,
                gravity = Alignment.Center
            ) { product ->
                Text(
                    text = product.quantity.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            },
            ColumnDefinition(
                title = "Quant. Mínima",
                weight = 1f,
                gravity = Alignment.Center
            ) { product ->
                Text(
                    text = product.minimumStock.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            },
            ColumnDefinition(
                title = "Preço de Compra",
                weight = 1.5f,
                gravity = Alignment.Center
            ) { product ->
                Text(
                    text = "R$ %.2f".format(product.costPrice),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            },
            ColumnDefinition(
                title = "Preço de Venda",
                weight = 1.5f,
                gravity = Alignment.Center
            ) { product ->
                Text(
                    text = "R$ %.2f".format(product.salePrice),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            }
        )
    }

    DataTable(
        columns = columns,
        rows = products,
        key = { it.idProduct },
        emptyMessage = "Você ainda não possui produtos cadastrados",
        modifier = modifier,
        onRowClick = onProductClick,
        rowActions = { product ->
            IconButton(
                onClick = { onOpenContextMenu(product.idProduct) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = MaterialIcons.Outlined.More_vert,
                    contentDescription = "Ações do produto",
                    modifier = Modifier.size(20.dp)
                )
            }
            DesktopProductContextMenu(
                expanded = contextMenuProductId == product.idProduct,
                onDismiss = onDismissContextMenu,
                onEdit = { onEdit(product) },
                onDuplicate = { onDuplicate(product) },
                onDelete = { onDelete(product) }
            )
        }
    )
}