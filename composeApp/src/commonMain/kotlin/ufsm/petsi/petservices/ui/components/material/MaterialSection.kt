package ufsm.petsi.petservices.ui.components.material

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.outlined.Add
import ufsm.petsi.petservices.models.Material
import ufsm.petsi.petservices.models.ProductMaterial

@Composable
fun MaterialSection(
    selectedMaterials: List<ProductMaterial>,
    availableMaterials: List<Material>,
    modifier: Modifier = Modifier,
    title: String = "Materiais",
    emptyText: String = "Nenhum material associado",
    onAddMaterial: (() -> Unit)? = null,
    addMaterialLabel: String = "Adicionar",
    trailing: @Composable (ProductMaterial) -> Unit = {}
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            if (onAddMaterial != null) {
                FilledTonalButton(
                    onClick = onAddMaterial,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = MaterialIcons.Outlined.Add,
                        contentDescription = "Adicionar material",
                        modifier = Modifier.size(18.dp)
                    )
                    Text(addMaterialLabel, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }

        if (selectedMaterials.isEmpty()) {
            Text(
                text = emptyText,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            selectedMaterials.forEach { productMaterial ->
                val material = availableMaterials.find {
                    it.idMaterial == productMaterial.idMaterial
                }
                MaterialListItem(
                    material = material ?: Material(
                        idMaterial = productMaterial.idMaterial,
                        name = productMaterial.idMaterial,
                        costPrice = 0.0,
                        metric = ""
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    trailing = {
                        Text(
                            text = productMaterial.quantity.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        trailing(productMaterial)
                    }
                )
            }
        }
    }
}