package ufsm.petsi.petservices.ui.products

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.outlined.More_vert
import ufsm.petsi.petservices.models.Product

private val COLUMN_WEIGHTS = listOf(3f, 1f, 1.5f, 1.5f, 1.5f)
private val COLUMN_GRAVITIES = listOf(
    Alignment.CenterStart,
    Alignment.Center,
    Alignment.Center,
    Alignment.Center,
    Alignment.Center
)
private val COLUMN_HEADERS = listOf(
    "Produtos | Materiais",
    "Quantidade",
    "Quant. Mínima",
    "Preço de Compra",
    "Preço de Venda"
)

@Composable
private fun RowScope.TableCell(
    columnIndex: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .weight(COLUMN_WEIGHTS[columnIndex])
            .padding(end = 8.dp),
        contentAlignment = COLUMN_GRAVITIES[columnIndex]
    ) {
        content()
    }
}

@Composable
fun ProductTable(
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
    Column(modifier = modifier.fillMaxSize()) {
        ProductTableHeader(
            modifier = Modifier.fillMaxWidth()
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        if (products.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Você ainda não possui produtos cadastrados",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(
                    items = products,
                    key = { _, product -> product.idProduct }
                ) { index, product ->
                    val rowBackground =
                        if (index % 2 == 0) MaterialTheme.colorScheme.surface
                        else MaterialTheme.colorScheme.surfaceContainerLow
                    ProductTableRow(
                        product = product,
                        background = rowBackground,
                        isContextMenuOpen = contextMenuProductId == product.idProduct,
                        onProductClick = { onProductClick(product) },
                        onOpenContextMenu = { onOpenContextMenu(product.idProduct) },
                        onDismissContextMenu = onDismissContextMenu,
                        onEdit = { onEdit(product) },
                        onDuplicate = { onDuplicate(product) },
                        onDelete = { onDelete(product) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
private fun ProductTableHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .height(IntrinsicSize.Min)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        COLUMN_HEADERS.forEachIndexed { index, label ->
            TableCell(index) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) {}
    }
}

@Composable
private fun ProductTableRow(
    product: Product,
    background: Color,
    isContextMenuOpen: Boolean,
    onProductClick: () -> Unit,
    onOpenContextMenu: () -> Unit,
    onDismissContextMenu: () -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onProductClick)
            .background(background)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TableCell(0) {
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
        }
        TableCell(1) {
            Text(
                text = product.quantity.toString(),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }
        TableCell(2) {
            Text(
                text = product.minimumStock.toString(),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }
        TableCell(3) {
            Text(
                text = "R$ %.2f".format(product.costPrice),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }
        TableCell(4) {
            Text(
                text = "R$ %.2f".format(product.salePrice),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onOpenContextMenu,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = MaterialIcons.Outlined.More_vert,
                    contentDescription = "Ações do produto",
                    modifier = Modifier.size(20.dp)
                )
            }
            ProductContextMenu(
                expanded = isContextMenuOpen,
                onDismiss = onDismissContextMenu,
                onEdit = onEdit,
                onDuplicate = onDuplicate,
                onDelete = onDelete
            )
        }
    }
}