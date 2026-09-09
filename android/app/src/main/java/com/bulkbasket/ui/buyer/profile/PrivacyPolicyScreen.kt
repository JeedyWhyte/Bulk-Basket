package com.bulkbasket.ui.buyer.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bulkbasket.ui.common.components.DetailScaffold
import com.bulkbasket.ui.theme.Dimensions

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailScaffold(
        title = "Privacy Policy",
        onBack = onBack,
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(Dimensions.paddingLarge)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium),
        ) {

            Text(
                text = "Last updated: September 2026",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            PolicySection(
                title = "1. Information We Collect",
                body = "We collect your name, email address, phone number, and delivery addresses when you register. We also collect usage data to improve the app experience.",
            )
            PolicySection(
                title = "2. How We Use Your Information",
                body = "Your information is used to process orders, connect you with sellers and riders, send order status notifications, and improve BulkBasket.",
            )
            PolicySection(
                title = "3. Data Sharing",
                body = "We share your delivery address and phone number with the seller and rider assigned to your order. We do not sell your personal data to third parties.",
            )
            PolicySection(
                title = "4. Data Storage",
                body = "Your data is stored securely on Supabase (PostgreSQL) with encryption at rest and in transit via SSL.",
            )
            PolicySection(
                title = "5. Your Rights",
                body = "You may request deletion of your account and data by contacting support@bulkbasket.com. Account deletion will be self-service in a future update.",
            )
            PolicySection(
                title = "6. Academic Context",
                body = "BulkBasket is currently an academic project built as part of a Mobile Application Development course. It is not yet a commercial product.",
            )
            PolicySection(
                title = "7. Contact",
                body = "For privacy questions, contact support@bulkbasket.com.",
            )
        }
    }
}

@Composable
private fun PolicySection(
    title: String,
    body: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensions.radiusMedium))
            .background(MaterialTheme.colorScheme.surface)
            .padding(Dimensions.paddingMedium),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 20.sp,
        )
    }
}
