package com.bulkbasket.ui.buyer.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bulkbasket.domain.model.Seller
import com.bulkbasket.ui.theme.Dimensions
import com.bulkbasket.ui.theme.Green50
import com.bulkbasket.ui.theme.Green600

@Composable
fun SellerCard(
    seller: Seller,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .width(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(Dimensions.radiusMedium),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.cardElevation),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.paddingMedium),
        ) {
            // Seller avatar placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Green50),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = seller.businessName.first().uppercaseChar().toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = Green600,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

            Text(
                text = seller.businessName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = seller.marketName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "★ ${seller.rating}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = if (seller.isOpen) "Open" else "Closed",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (seller.isOpen)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}