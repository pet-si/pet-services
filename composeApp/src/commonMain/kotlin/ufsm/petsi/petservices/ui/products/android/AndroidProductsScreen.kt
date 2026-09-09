package ufsm.petsi.petservices.ui.products.android

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.outlined.Add
import com.composables.icons.materialicons.outlined.Delete
import com.composables.icons.materialicons.outlined.Edit
import org.koin.compose.viewmodel.koinViewModel
import ufsm.petsi.petservices.models.Product
import ufsm.petsi.petservices.ui.components.product.ProductListItem
import ufsm.petsi.petservices.ui.products.ProductsEffects
import ufsm.petsi.petservices.ui.products.ProductsIntent
import ufsm.petsi.petservices.ui.products.ProductsViewModel

@Composable
fun AndroidProductsScreen(
    onCreateProduct: () -> Unit,
    onProductSelected: (String) -> Unit,
    onProductEdit: (String) -> Unit,
) {
    val viewModel: ProductsViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effects ->
            when (effects) {
                is ProductsEffects.NavigateToProductCreation -> onCreateProduct()
                is ProductsEffects.NavigateToProductDetails -> onProductSelected(effects.productId)
                is ProductsEffects.NavigateToProductEdit -> onProductEdit(effects.productId)
            }
        }
    }

    AnimatedContent(
        targetState = uiState.products.isNotEmpty(),
        modifier = Modifier.fillMaxSize()
    ) { hasProducts ->
        Box(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            if (hasProducts) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.products,
                        key = { it.idProduct }
                    ) { product ->
                        val dismissState = rememberSwipeToDismissBoxState()

                        LaunchedEffect(dismissState.currentValue) {
                            when (dismissState.currentValue) {
                                SwipeToDismissBoxValue.EndToStart -> {
                                    viewModel.handleIntent(ProductsIntent.OnProductDelete(product.idProduct))
                                    dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                                }
                                SwipeToDismissBoxValue.StartToEnd -> {
                                    viewModel.handleIntent(ProductsIntent.OnProductEdit(product.idProduct))
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
                            ProductListItem(
                                product = product,
                                onClick = {
                                    viewModel.handleIntent(ProductsIntent.OnProductClick(product.idProduct))
                                }
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "Você ainda não possui produtos cadastrados",
                    modifier = Modifier.padding(top = 32.dp)
                )
            }
            FloatingActionButton(
                onClick = {
                    viewModel.handleIntent(ProductsIntent.OnAddProductClick)
                },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
            ) {
                Icon(
                    imageVector = MaterialIcons.Outlined.Add,
                    contentDescription = "Adicionar Produto"
                )
            }
        }
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
