package com.bulkbasket.ui.common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bulkbasket.ui.theme.Dimensions

@Composable
fun BulkBasketCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(Dimensions.radiusMedium),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = Dimensions.cardElevation,
            ),
            border = BorderStroke(
                width = Dimensions.cardBorderWidth,
                color = MaterialTheme.colorScheme.outline,
            ),
            content = content,
        )
    } else {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(Dimensions.radiusMedium),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = Dimensions.cardElevation,
            ),
            border = BorderStroke(
                width = Dimensions.cardBorderWidth,
                color = MaterialTheme.colorScheme.outline,
            ),
            content = content,
        )
    }
}
