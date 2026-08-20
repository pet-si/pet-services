package ufsm.petsi.petservices.ui.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.outlined.Add
import com.composables.icons.materialicons.outlined.Delete
import com.composables.icons.materialicons.outlined.Edit
import org.koin.compose.viewmodel.koinViewModel
import ufsm.petsi.petservices.models.Product
import ufsm.petsi.petservices.ui.components.product.ProductListItem
import ufsm.petsi.petservices.ui.products.create.CreateProductContent
import ufsm.petsi.petservices.ui.products.view.ViewProductContent

@Composable
fun DesktopProductLayout() {
    val viewModel: ProductsViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsState().value

    var selectedProductId by remember { mutableStateOf<String?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.products) {
        val id = selectedProductId
        if (id != null && uiState.products.none { it.idProduct == id }) {
            selectedProductId = null
            isEditing = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effects ->
            when (effects) {
                is ProductsEffects.NavigateToProductDetails -> {
                    selectedProductId = effects.productId
                    isEditing = false
                }

                is ProductsEffects.NavigateToProductEdit -> {
                    selectedProductId = effects.productId
                    isEditing = true
                }

                is ProductsEffects.NavigateToProductCreation -> {
                    selectedProductId = null
                    isEditing = true
                }
            }
        }
    }

    Scaffold { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ProductListPane(
                products = uiState.products,
                selectedProductId = selectedProductId,
                onProductClick = { productId ->
                    selectedProductId = productId
                    isEditing = false
                },
                onEditClick = { productId ->
                    selectedProductId = productId
                    isEditing = true
                },
                onDeleteClick = { productId ->
                    viewModel.handleIntent(ProductsIntent.OnProductDelete(productId))
                },
                onCreateProduct = {
                    selectedProductId = null
                    isEditing = true
                },
                modifier = Modifier.weight(0.25f)
            )

            VerticalDivider()

            DetailPane(
                selectedProductId = selectedProductId,
                isEditing = isEditing,
                onNavigateToEdit = { id ->
                    selectedProductId = id
                    isEditing = true
                },
                onNavigateToViewProduct = { id ->
                    selectedProductId = id
                    isEditing = false
                },
                onFormSaved = {
                    selectedProductId = null
                    isEditing = false
                },
                modifier = Modifier.weight(0.75f)
            )
        }

        if (uiState.showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.handleIntent(ProductsIntent.DismissDeleteDialog) },
                title = { Text("Excluir Produto") },
                text = {
                    Text(
                        "Você tem certeza que deseja excluir o produto" +
                                uiState.productToDelete?.let { " \"${it.name}\"" }.orEmpty() +
                                "? Esta ação não pode ser desfeita."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.handleIntent(ProductsIntent.ConfirmDeleteProduct) }
                    ) {
                        Text("Excluir", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.handleIntent(ProductsIntent.DismissDeleteDialog) }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
private fun ProductListPane(
    products: List<Product>,
    selectedProductId: String?,
    onProductClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onCreateProduct: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize().padding(8.dp)
    ) {
        if (products.isEmpty()) {
            Text(
                text = "Você ainda não possui produtos cadastrados",
                modifier = Modifier.padding(top = 32.dp).padding(horizontal = 16.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = products,
                    key = { it.idProduct }
                ) { product ->
                    val isSelected = product.idProduct == selectedProductId
                    ProductListItem(
                        product = product,
                        onClick = { onProductClick(product.idProduct) },
                        trailing = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                FilledTonalIconButton(
                                    onClick = { onEditClick(product.idProduct) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = MaterialIcons.Outlined.Edit,
                                        contentDescription = "Editar",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                FilledTonalIconButton(
                                    onClick = { onDeleteClick(product.idProduct) },
                                    modifier = Modifier.size(36.dp),
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer,
                                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                ) {
                                    Icon(
                                        imageVector = MaterialIcons.Outlined.Delete,
                                        contentDescription = "Excluir",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        modifier = if (isSelected) Modifier.background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(12.dp)
                        ) else Modifier
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onCreateProduct,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(
                imageVector = MaterialIcons.Outlined.Add,
                contentDescription = "Adicionar Produto"
            )
        }
    }
}

@Composable
private fun DetailPane(
    selectedProductId: String?,
    isEditing: Boolean,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToViewProduct: (String) -> Unit,
    onFormSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            selectedProductId != null && !isEditing -> {
                ViewProductContent(
                    productId = selectedProductId,
                    onNavigateToEdit = onNavigateToEdit,
                )
            }

            isEditing -> {
                CreateProductContent(
                    productId = selectedProductId,
                    onNavigateBack = onFormSaved,
                    onNavigateToViewProduct = onNavigateToViewProduct
                )
            }

            else -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Selecione um produto para visualizar",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
