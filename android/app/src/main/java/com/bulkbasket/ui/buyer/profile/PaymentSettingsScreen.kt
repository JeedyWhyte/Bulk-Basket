package com.bulkbasket.ui.buyer.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bulkbasket.domain.model.PaymentMethod
import com.bulkbasket.ui.common.components.DetailScaffold
import com.bulkbasket.ui.common.components.SectionHeader
import com.bulkbasket.ui.theme.Dimensions

@Composable
fun PaymentSettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PaymentSettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    DetailScaffold(
        title = "Payment Settings",
        onBack = onBack,
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(Dimensions.paddingLarge),
            verticalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium),
        ) {

            SectionHeader(title = "Cash Payment")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimensions.radiusMedium))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(Dimensions.paddingMedium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium),
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalShipping,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp),
                )
                Column {
                    Text(
                        text = "Cash on Delivery",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "Pay when your order arrives",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            SectionHeader(title = "Saved Demo Cards")

            Text(
                text = "These are simulated cards for the demo payment gateway — " +
                    "no real card data is stored or charged.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(Dimensions.paddingMedium))
            } else if (state.methods.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Dimensions.radiusMedium))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(Dimensions.paddingMedium),
                ) {
                    Text(
                        text = "No demo cards saved yet",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Add one below, or save a card from Checkout.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)) {
                    state.methods.forEach { method ->
                        SavedCardRow(method, onRemove = { viewModel.removeMethod(method.id) })
                    }
                }
            }

            OutlinedButton(
                onClick = { viewModel.showAddDialog() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimensions.radiusMedium),
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add a demo card")
            }

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }

    if (state.showAddDialog) {
        AddCardDialog(
            isSaving = state.isSaving,
            onDismiss = { viewModel.hideAddDialog() },
            onSave = { brand, last4, month, year ->
                viewModel.addMethod(brand, last4, month, year)
            },
        )
    }
}

@Composable
private fun SavedCardRow(method: PaymentMethod, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensions.radiusMedium))
            .background(MaterialTheme.colorScheme.surface)
            .padding(Dimensions.paddingMedium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium),
    ) {
        Icon(
            imageVector = Icons.Filled.CreditCard,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${method.brand.replaceFirstChar { it.uppercase() }} •••• ${method.last4}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Expires %02d/%d".format(method.expiryMonth, method.expiryYear % 100),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Remove",
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun AddCardDialog(
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (brand: String, last4: String, month: Int, year: Int) -> Unit,
) {
    var number by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add a demo card") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)) {
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it.filter { c -> c.isDigit() }.take(16) },
                    label = { Text("Card number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = expiry,
                    onValueChange = { expiry = it.filter { c -> c.isDigit() || c == '/' }.take(5) },
                    label = { Text("Expiry MM/YY") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parts = expiry.split("/")
                    val month = parts.getOrNull(0)?.toIntOrNull()
                    val year = parts.getOrNull(1)?.toIntOrNull()?.let { 2000 + it }
                    val brand = when {
                        number.startsWith("4") -> "visa"
                        number.startsWith("5") -> "mastercard"
                        else -> "verve"
                    }
                    when {
                        number.length < 12 -> error = "Enter a valid card number."
                        month == null || month !in 1..12 || year == null ->
                            error = "Enter a valid expiry as MM/YY."
                        else -> onSave(brand, number.takeLast(4), month, year)
                    }
                },
                enabled = !isSaving,
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
