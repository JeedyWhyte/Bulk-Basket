package com.bulkbasket.ui.buyer.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.bulkbasket.ui.common.components.DetailScaffold
import com.bulkbasket.ui.common.components.SectionHeader
import com.bulkbasket.ui.theme.Dimensions

@Composable
fun NotificationPreferencesScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationPreferencesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.state.collectAsState()

    DetailScaffold(
        title = "Notification Preferences",
        onBack = onBack,
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(Dimensions.paddingLarge),
            verticalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium),
        ) {

            SectionHeader(title = "Push Notifications")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimensions.radiusMedium))
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                NotificationToggleRow(
                    title = "Order Updates",
                    subtitle = "Status changes for your orders",
                    checked = uiState.orderUpdates,
                    onCheckedChange = { viewModel.setOrderUpdates(it) },
                )
                HorizontalDivider(
                    modifier = Modifier.padding(
                        horizontal = Dimensions.paddingMedium
                    ),
                    color = MaterialTheme.colorScheme.outline,
                )
                NotificationToggleRow(
                    title = "Promotions",
                    subtitle = "Deals and special offers",
                    checked = uiState.promotions,
                    onCheckedChange = { viewModel.setPromotions(it) },
                )
                HorizontalDivider(
                    modifier = Modifier.padding(
                        horizontal = Dimensions.paddingMedium
                    ),
                    color = MaterialTheme.colorScheme.outline,
                )
                NotificationToggleRow(
                    title = "New Arrivals",
                    subtitle = "New products from your sellers",
                    checked = uiState.newArrivals,
                    onCheckedChange = { viewModel.setNewArrivals(it) },
                )
            }
        }
    }
}

@Composable
private fun NotificationToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimensions.paddingMedium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
            ),
        )
    }
}
