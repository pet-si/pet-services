package ufsm.petsi.petservices.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

data class ColumnDefinition<T>(
    val title: String,
    val weight: Float,
    val gravity: Alignment,
    val cell: @Composable (T) -> Unit
)

@Composable
fun <T> DataTable(
    columns: List<ColumnDefinition<T>>,
    rows: List<T>,
    key: (T) -> Any,
    emptyMessage: String,
    modifier: Modifier = Modifier,
    onRowClick: ((T) -> Unit)? = null,
    rowActions: (@Composable (T) -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxSize()) {
        TableHeader(
            columns = columns,
            hasActions = rowActions != null
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        if (rows.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(
                    items = rows,
                    key = { _, row -> key(row) }
                ) { index, row ->
                    val rowBackground =
                        if (index % 2 == 0) MaterialTheme.colorScheme.surface
                        else MaterialTheme.colorScheme.surfaceContainerLow
                    TableRow(
                        columns = columns,
                        row = row,
                        background = rowBackground,
                        onRowClick = onRowClick?.let { { it(row) } },
                        rowActions = rowActions
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
private fun <T> TableHeader(
    columns: List<ColumnDefinition<T>>,
    hasActions: Boolean
) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .height(IntrinsicSize.Min)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        columns.forEach { column ->
            Box(
                modifier = Modifier
                    .weight(column.weight)
                    .padding(end = 8.dp),
                contentAlignment = column.gravity
            ) {
                Text(
                    text = column.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (hasActions) {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {}
        }
    }
}

@Composable
private fun <T> TableRow(
    columns: List<ColumnDefinition<T>>,
    row: T,
    background: Color,
    onRowClick: (() -> Unit)?,
    rowActions: (@Composable (T) -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onRowClick != null) Modifier.clickable(onClick = onRowClick) else Modifier)
            .background(background)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        columns.forEach { column ->
            Box(
                modifier = Modifier
                    .weight(column.weight)
                    .padding(end = 8.dp),
                contentAlignment = column.gravity
            ) {
                column.cell(row)
            }
        }
        rowActions?.let { actions ->
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                actions(row)
            }
        }
    }
}