package ufsm.petsi.petservices.ui.products

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.outlined.`4k_plus`
import com.composables.icons.materialicons.outlined.Add
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductsScreen() {
    val viewModel: ProductsViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsState().value
    AnimatedContent(
        targetState = uiState.products.isNotEmpty(),
        modifier = Modifier.fillMaxSize()
    ) { state ->
        Box(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            if (state) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.products) {
                        ProductItem(name = it.name)
                    }
                }
            } else {
                Text(text = "Você ainda não possui produtos cadastrados",
                    modifier = Modifier.padding(top = 32.dp))
            }
            FloatingActionButton(
                onClick = {
                    viewModel.handleIntent(ProductsIntent.OnAddProductClick)
                },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
            ) {
                Icon(imageVector = MaterialIcons.Outlined.Add, contentDescription = "Adicionar Produto")
            }
        }
    }
}

@Composable
private fun ProductItem(name: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(12.dp),
        onClick = {

        }
    ) {
        Text(text = name, modifier = Modifier.padding(12.dp))
    }
}