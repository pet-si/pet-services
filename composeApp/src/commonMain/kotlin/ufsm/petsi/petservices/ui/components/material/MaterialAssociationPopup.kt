package ufsm.petsi.petservices.ui.components.material

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ufsm.petsi.petservices.models.Material
import ufsm.petsi.petservices.repository.DataResult
import ufsm.petsi.petservices.repository.implementations.MaterialRepository
import ufsm.petsi.petservices.ui.components.dialog.ModalPopup
import ufsm.petsi.petservices.ui.components.textField.DefaultTextField
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun MaterialAssociationPopup(
    availableMaterials: List<Material>,
    onDismiss: () -> Unit,
    onConfirm: (Material, Double) -> Unit,
    onMaterialCreated: (Material) -> Unit,
    modifier: Modifier = Modifier
) {
    val repository: MaterialRepository = koinInject()
    val scope = rememberCoroutineScope()

    var showNewMaterialForm by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedMaterial by remember { mutableStateOf<Material?>(null) }
    var quantity by remember { mutableStateOf("") }

    var newName by remember { mutableStateOf("") }
    var newCostPrice by remember { mutableStateOf("") }
    var newMinimumStock by remember { mutableStateOf("") }
    var newMetric by remember { mutableStateOf("") }
    var newNameError by remember { mutableStateOf<String?>(null) }
    var newCostPriceError by remember { mutableStateOf<String?>(null) }
    var newMinimumStockError by remember { mutableStateOf<String?>(null) }
    var newMetricError by remember { mutableStateOf<String?>(null) }

    val filteredMaterials = availableMaterials.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    fun switchMode() {
        showNewMaterialForm = !showNewMaterialForm
        selectedMaterial = null
        quantity = ""
        searchQuery = ""
    }

    fun validateNewMaterial(): Boolean {
        var hasError = false
        newNameError = if (newName.isBlank()) {
            hasError = true
            "Nome é obrigatório"
        } else null
        newCostPriceError = if (newCostPrice.isBlank()) {
            hasError = true
            "Valor é obrigatório"
        } else null
        newMinimumStockError = if (newMinimumStock.isBlank()) {
            hasError = true
            "Estoque é obrigatório"
        } else null
        newMetricError = if (newMetric.isBlank()) {
            hasError = true
            "Métrica é obrigatória"
        } else null
        return !hasError
    }

    fun createAndSelect() {
        if (!validateNewMaterial()) return

        val material = Material(
            name = newName.trim(),
            costPrice = newCostPrice.toDoubleOrNull()?.div(100) ?: 0.0,
            minimumStock = newMinimumStock.toIntOrNull() ?: 0,
            metric = newMetric.trim()
        )
        scope.launch {
            when (val result = repository.insertMaterial(material)) {
                is DataResult.Success -> {
                    onMaterialCreated(material)
                    selectedMaterial = material
                    showNewMaterialForm = false
                    searchQuery = ""
                    quantity = ""
                    newName = ""
                    newCostPrice = ""
                    newMinimumStock = ""
                    newMetric = ""
                    newNameError = null
                    newCostPriceError = null
                    newMinimumStockError = null
                    newMetricError = null
                }

                is DataResult.Error -> Unit
                is DataResult.Loading -> Unit
            }
        }
    }

    ModalPopup(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (showNewMaterialForm) "Novo Material" else "Adicionar Material",
                style = MaterialTheme.typography.titleLarge
            )
        },
        buttons = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
            if (showNewMaterialForm) {
                Button(
                    onClick = { createAndSelect() },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Criar e Selecionar")
                }
            } else {
                val material = selectedMaterial
                Button(
                    onClick = {
                        val materialValue = material ?: return@Button
                        val quantityValue = quantity.toDoubleOrNull() ?: return@Button
                        onConfirm(materialValue, quantityValue)
                    },
                    enabled = material != null && quantity.toDoubleOrNull() != null,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirmar")
                }
            }
        },
        modifier = modifier
    ) {
        if (showNewMaterialForm) {
            MaterialForm(
                name = newName,
                onNameChange = { newName = it },
                costPrice = newCostPrice,
                onCostPriceChange = { input ->
                    val clean = input.filter { c -> c.isDigit() }
                    if (clean.length <= 9) newCostPrice = clean
                },
                minimumStock = newMinimumStock,
                onMinimumStockChange = { newMinimumStock = it.filter { c -> c.isDigit() } },
                metric = newMetric,
                onMetricChange = { newMetric = it },
                nameError = newNameError,
                costPriceError = newCostPriceError,
                minimumStockError = newMinimumStockError,
                metricError = newMetricError
            )
        } else {
            MaterialPicker(
                materials = filteredMaterials,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onMaterialSelected = { selectedMaterial = it },
                selectedMaterialId = selectedMaterial?.idMaterial,
                showTitle = false
            )

            selectedMaterial?.let { material ->
                DefaultTextField(
                    label = { Text("Quantidade") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    value = quantity,
                    onValueChange = { quantity = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        FilledTonalButton(
            onClick = { switchMode() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (showNewMaterialForm) "Buscar material existente" else "Criar novo material"
            )
        }
    }
}