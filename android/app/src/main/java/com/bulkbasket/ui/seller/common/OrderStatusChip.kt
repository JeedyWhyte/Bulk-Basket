package com.bulkbasket.ui.seller.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bulkbasket.ui.theme.Dimensions

@Composable
fun OrderStatusChip(status: String) {
    val (backgroundColor, textColor) = when (status) {
        "pending"    -> Color(0xFFFFF3CD) to Color(0xFF856404)
        "confirmed"  -> Color(0xFFD1ECF1) to Color(0xFF0C5460)
        "preparing"  -> Color(0xFFD4EDDA) to Color(0xFF155724)
        "ready"      -> Color(0xFFCCE5FF) to Color(0xFF004085)
        "in_transit" -> Color(0xFFE2D9F3) to Color(0xFF4A235A)
        "delivered"  -> Color(0xFFD4EDDA) to Color(0xFF155724)
        "cancelled"  -> Color(0xFFF8D7DA) to Color(0xFF721C24)
        else         -> Color(0xFFE2E3E5) to Color(0xFF383D41)
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(Dimensions.radiusFull),
    ) {
        Text(
            text = status.replace("_", " ").replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            modifier = Modifier.padding(
                horizontal = Dimensions.paddingSmall,
                vertical = 4.dp,
            ),
        )
    }
}