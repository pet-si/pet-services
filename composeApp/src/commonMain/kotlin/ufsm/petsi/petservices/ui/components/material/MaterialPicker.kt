package ufsm.petsi.petservices.ui.components.material

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.outlined.Check
import ufsm.petsi.petservices.models.Material
import ufsm.petsi.petservices.ui.components.textField.DefaultTextField

@Composable
fun MaterialPicker(
    materials: List<Material>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onMaterialSelected: (Material) -> Unit,
    modifier: Modifier = Modifier,
    selectedMaterialId: String? = null,
) {
    Column(modifier = modifier) {
        DefaultTextField(
            label = { Text("Buscar material") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            value = searchQuery,
            onValueChange = onSearchQueryChange
        )

        if (materials.isEmpty()) {
            Text(
                text = "Nenhum material encontrado",
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(materials) { material ->
                    val isSelected = material.idMaterial == selectedMaterialId
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
                        onClick = { onMaterialSelected(material) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = material.name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "R$ %.2f".format(material.costPrice),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "/ ${material.metric}",
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
}
