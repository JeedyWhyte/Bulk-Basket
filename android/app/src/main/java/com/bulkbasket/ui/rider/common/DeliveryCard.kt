package com.bulkbasket.ui.rider.common

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
import com.bulkbasket.domain.model.Delivery
import com.bulkbasket.ui.theme.Dimensions

@Composable
fun DeliveryCard(
    delivery: Delivery,
    onAccept: ((Int) -> Unit)? = null,
    onUpdateStatus: ((Int, String) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val nextStatus = when (delivery.status) {
        "assigned"  -> "picked_up"
        "picked_up" -> "delivered"
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Delivery #${delivery.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = delivery.status.replace("_", " ")
                        .replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

            // Delivery address
            if (delivery.street != null) {
                Text(
                    text = "Deliver to:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${delivery.street}, ${delivery.city}, ${delivery.state}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

            Text(
                text = "Order Total: ₦${delivery.orderTotal}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

            // Action buttons
            if (onAccept != null && delivery.status == "pending") {
                Button(
                    onClick = { onAccept(delivery.id) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimensions.radiusSmall),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Text("Accept Delivery")
                }
            }

            if (onUpdateStatus != null && nextStatus != null) {
                Button(
                    onClick = { onUpdateStatus(delivery.id, nextStatus) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimensions.radiusSmall),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Text(
                        text = when (nextStatus) {
                            "picked_up" -> "Mark Picked Up"
                            "delivered" -> "Mark Delivered"
                            else -> "Update Status"
                        }
                    )
                }
            }
        }
    }
}