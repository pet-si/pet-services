package ufsm.petsi.petservices.ui.products.desktop

import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.outlined.Content_copy
import com.composables.icons.materialicons.outlined.Delete
import com.composables.icons.materialicons.outlined.Edit

@Composable
fun DesktopProductContextMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = { Text("Editar") },
            onClick = { onDismiss(); onEdit() },
            leadingIcon = {
                Icon(
                    imageVector = MaterialIcons.Outlined.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        )
        DropdownMenuItem(
            text = { Text("Copiar") },
            onClick = { onDismiss(); onDuplicate() },
            leadingIcon = {
                Icon(
                    imageVector = MaterialIcons.Outlined.Content_copy,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    "Excluir",
                    color = MaterialTheme.colorScheme.error
                )
            },
            onClick = { onDismiss(); onDelete() },
            leadingIcon = {
                Icon(
                    imageVector = MaterialIcons.Outlined.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        )
    }
}
