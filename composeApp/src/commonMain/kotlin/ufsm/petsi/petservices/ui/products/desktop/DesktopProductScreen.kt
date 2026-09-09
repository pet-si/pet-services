package ufsm.petsi.petservices.ui.products.desktop

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.outlined.Add
import com.composables.icons.materialicons.outlined.Search
import org.koin.compose.viewmodel.koinViewModel
import ufsm.petsi.petservices.ui.components.dialog.ModalPopup
import ufsm.petsi.petservices.ui.components.textField.DefaultTextField
import ufsm.petsi.petservices.ui.products.ProductsIntent
import ufsm.petsi.petservices.ui.products.ProductsViewModel

@Composable
fun DesktopProductScreen(modifier: Modifier = Modifier) {
    val viewModel: ProductsViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsState().value

    var searchQuery by remember { mutableStateOf("") }
    var contextMenuProductId by remember { mutableStateOf<String?>(null) }
    var editingProductId by remember { mutableStateOf<String?>(null) }
    var showCreatePopup by remember { mutableStateOf(false) }
    var viewingProductId by remember { mutableStateOf<String?>(null) }

    val filteredProducts = remember(searchQuery, uiState.products) {
        if (searchQuery.isBlank()) uiState.products
        else uiState.products.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp, vertical = 24.dp)
        ) {
            ScreenHeader()

            DesktopProductMetricsCards(
                productCount = uiState.products.size,
                modifier = Modifier.padding(top = 24.dp)
            )

            SearchAndActionsBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onCreateClick = {
                    editingProductId = null
                    showCreatePopup = true
                },
                modifier = Modifier.padding(top = 24.dp)
            )

            DesktopProductTable(
                products = filteredProducts,
                contextMenuProductId = contextMenuProductId,
                onOpenContextMenu = { contextMenuProductId = it },
                onDismissContextMenu = { contextMenuProductId = null },
                onProductClick = { viewingProductId = it.idProduct },
                onEdit = { product ->
                    editingProductId = product.idProduct
                    showCreatePopup = true
                },
                onDuplicate = { product ->
                    viewModel.handleIntent(ProductsIntent.OnProductDuplicate(product.idProduct))
                },
                onDelete = { product ->
                    viewModel.handleIntent(ProductsIntent.OnProductDelete(product.idProduct))
                },
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }

    if (showCreatePopup) {
        DesktopCreateProductPopup(
            productId = editingProductId,
            onDismiss = { showCreatePopup = false }
        )
    }

    viewingProductId?.let { productId ->
        DesktopProductViewPopup(
            productId = productId,
            onDismiss = { viewingProductId = null },
            onEdit = { id ->
                viewingProductId = null
                editingProductId = id
                showCreatePopup = true
            }
        )
    }

    if (uiState.showDeleteDialog) {
        ModalPopup(
            onDismissRequest = { viewModel.handleIntent(ProductsIntent.DismissDeleteDialog) },
            title = {
                Text(
                    text = "Excluir Produto",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            buttons = {
                TextButton(
                    onClick = { viewModel.handleIntent(ProductsIntent.DismissDeleteDialog) }
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = { viewModel.handleIntent(ProductsIntent.ConfirmDeleteProduct) },
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
                text = "Você tem certeza que deseja excluir o produto" +
                    uiState.productToDelete?.let { " \"${it.name}\"" }.orEmpty() +
                    "? Esta ação não pode ser desfeita.",
                style = MaterialTheme.typography.bodyMedium
            )
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
                text = "Gerenciamento de estoque",
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
            label = { Text("Buscar produto...") },
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