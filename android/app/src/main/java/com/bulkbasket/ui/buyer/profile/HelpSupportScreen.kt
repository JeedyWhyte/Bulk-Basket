package com.bulkbasket.ui.buyer.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bulkbasket.ui.common.components.DetailScaffold
import com.bulkbasket.ui.common.components.SectionHeader
import com.bulkbasket.ui.theme.Dimensions

private data class FaqItem(val question: String, val answer: String)

private val faqs = listOf(
    FaqItem(
        "How do I place an order?",
        "Browse nearby sellers on the Home screen, tap a seller to see their products, add items to your cart, and tap Place Order at checkout.",
    ),
    FaqItem(
        "How is payment handled?",
        "BulkBasket currently supports Cash on Delivery only. You pay the rider when your order arrives. Card payments are coming soon.",
    ),
    FaqItem(
        "Can I cancel an order?",
        "You can cancel an order while it is still Pending. Once a seller confirms the order, cancellation is no longer available.",
    ),
    FaqItem(
        "How do I track my order?",
        "Go to Orders in the bottom navigation. Your order status updates as the seller prepares it and a rider picks it up.",
    ),
    FaqItem(
        "What if my order is wrong or damaged?",
        "Contact the seller directly through the app. Dispute resolution will be added in a future update.",
    ),
    FaqItem(
        "How do I become a seller?",
        "Register with the Seller role, set up your profile with your business and market name, and start adding products.",
    ),
)

@Composable
fun HelpSupportScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailScaffold(
        title = "Help & Support",
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

            SectionHeader(title = "Frequently Asked Questions")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimensions.radiusMedium))
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                faqs.forEachIndexed { index, faq ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimensions.paddingMedium),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = faq.question,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = faq.answer,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (index < faqs.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(
                                horizontal = Dimensions.paddingMedium
                            ),
                            color = MaterialTheme.colorScheme.outline,
                        )
                    }
                }
            }

            SectionHeader(title = "Contact Us")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimensions.radiusMedium))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(Dimensions.paddingMedium),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "BulkBasket Support",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "support@bulkbasket.com",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "This is an academic project built by the BulkBasket team at Polaruuma.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
