package com.bulkbasket.ui.seller.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.bulkbasket.domain.model.Order
import com.bulkbasket.ui.theme.Dimensions

@Composable
fun OrderCard(
    order: Order,
    onUpdateStatus: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val nextStatus = when (order.status) {
        "pending"   -> "confirmed"
        "confirmed" -> "preparing"
        "preparing" -> "ready"
        else        -> null
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.radiusMedium),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimensions.cardElevation
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.paddingMedium),
        ) {
            // Order header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Order #${order.id.take(8)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                OrderStatusChip(status = order.status)
            }

            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

            Text(
                text = "Buyer: ${order.buyerName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingXSmall))

            // Order items
            order.items.forEach { item ->
                Text(
                    text = "${item.quantity}x ${item.productName} — ₦${item.totalPrice}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Total: ₦${order.total}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                if (nextStatus != null) {
                    Button(
                        onClick = { onUpdateStatus(order.id, nextStatus) },
                        shape = RoundedCornerShape(Dimensions.radiusSmall),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                    ) {
                        Text(
                            text = "Mark ${nextStatus.replaceFirstChar { it.uppercase() }}",
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }

            if (order.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(Dimensions.paddingXSmall))
                Text(
                    text = "Note: ${order.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}