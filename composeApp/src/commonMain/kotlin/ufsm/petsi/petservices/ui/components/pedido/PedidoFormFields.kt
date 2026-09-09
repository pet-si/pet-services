package ufsm.petsi.petservices.ui.components.pedido

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.outlined.Add
import com.composables.icons.materialicons.outlined.Check
import com.composables.icons.materialicons.outlined.Close
import ufsm.petsi.petservices.models.Product
import ufsm.petsi.petservices.models.PurchaseOrderProduct
import ufsm.petsi.petservices.ui.components.textField.DefaultTextField

@Composable
fun PedidoFormFields(
    date: String,
    onDateChange: (String) -> Unit,
    products: List<PurchaseOrderProduct>,
    availableProducts: List<Product>,
    onOpenProductPicker: () -> Unit,
    onRemoveProduct: (String) -> Unit,
    dateError: String?,
    productsError: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        DefaultTextField(
            label = { Text("Data do Pedido") },
            placeholder = { Text("dd/mm/aaaa") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            value = date,
            onValueChange = onDateChange,
            isError = dateError != null,
            errorMessage = dateError ?: ""
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Produtos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            FilledTonalButton(
                onClick = onOpenProductPicker,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = MaterialIcons.Outlined.Add,
                        contentDescription = "Adicionar Produto"
                    )
                    Text("Adicionar")
                }
            }
        }

        if (productsError != null) {
            Text(
                text = productsError,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        if (products.isEmpty() && productsError == null) {
            Text(
                text = "Nenhum produto adicionado",
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        products.forEach { line ->
            val product = availableProducts.firstOrNull { it.idProduct == line.idProduct }
            PedidoLineItem(
                name = product?.name ?: line.idProduct,
                quantity = line.quantity,
                price = product?.salePrice,
                onRemove = { onRemoveProduct(line.idProduct) },
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun ProductPickerSection(
    products: List<Product>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onProductSelected: (Product) -> Unit,
    selectedProduct: Product?,
    productQuantity: String,
    onProductQuantityChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    quantityError: String?,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRetry: () -> Unit = {}
) {
    Column(modifier = modifier.padding(top = 8.dp)) {
        DefaultTextField(
            label = { Text("Buscar produto") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            value = searchQuery,
            onValueChange = onSearchQueryChange
        )

        when {
            isLoading -> {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    OutlinedButton(
                        onClick = onRetry,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Tentar novamente")
                    }
                }
            }

            products.isEmpty() -> {
                Text(
                    text = if (searchQuery.isBlank()) "Nenhum produto cadastrado" else "Nenhum produto encontrado",
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(products) { product ->
                        val isSelected = product.idProduct == selectedProduct?.idProduct
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .then(
                                    if (isSelected) Modifier.background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        RoundedCornerShape(12.dp)
                                    ) else Modifier
                                ),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(12.dp),
                            onClick = { onProductSelected(product) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "R$ %.2f".format(product.salePrice),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Estoque: ${product.quantity ?: 0}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = MaterialIcons.Outlined.Check,
                                        contentDescription = "Selecionado",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        selectedProduct?.let { product ->
            Text(
                text = "Selecionado: ${product.name}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            DefaultTextField(
                label = { Text("Quantidade") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                value = productQuantity,
                onValueChange = onProductQuantityChange,
                isError = quantityError != null,
                errorMessage = quantityError ?: ""
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirmar")
                }
            }
        }
    }
}

@Composable
private fun PedidoLineItem(
    name: String,
    quantity: Int,
    price: Double?,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = price?.let { "R$ %.2f".format(it) } ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "x$quantity",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 8.dp)
            )
            FilledTonalButton(
                modifier = Modifier.padding(start = 8.dp),
                onClick = onRemove,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = MaterialIcons.Outlined.Close,
                    contentDescription = "Remover Produto"
                )
            }
        }
    }
}