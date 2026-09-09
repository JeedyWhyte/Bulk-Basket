package com.bulkbasket.ui.buyer.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.bulkbasket.ui.buyer.cart.CartViewModel
import com.bulkbasket.ui.theme.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onOrderSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CheckoutViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val cartState by cartViewModel.state.collectAsState()

    LaunchedEffect(state.orderPlaced) {
        if (state.orderPlaced) onOrderSuccess()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(
                        horizontal = Dimensions.paddingLarge,
                        vertical = 4.dp,
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Text(
                        text = "Checkout",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimensions.paddingLarge),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "₦${"%.2f".format(cartState.total)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

                    if (state.error != null) {
                        Text(
                            text = state.error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(
                                bottom = Dimensions.paddingSmall
                            ),
                        )
                    }

                    Button(
                        onClick = { viewModel.placeOrder(cartViewModel) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimensions.buttonHeight),
                        shape = RoundedCornerShape(Dimensions.radiusFull),
                        enabled = !state.isPlacingOrder &&
                                state.selectedAddressId != null &&
                                cartState.items.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                    ) {
                        if (state.isPlacingOrder) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(
                                text = "Place Order",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        },
    ) { innerPadding ->

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(Dimensions.paddingLarge),
            verticalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium),
        ) {

            // Delivery address section
            item {
                Text(
                    text = "Delivery Address",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (state.isLoadingAddresses) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (state.addresses.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                    ) {
                        Text(
                            text = "No delivery address yet — add one below to continue.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(Dimensions.paddingMedium),
                        )
                    }
                }
            } else {
                items(state.addresses) { address ->
                    val isSelected = state.selectedAddressId == address.id
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimensions.radiusMedium),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outline,
                        ),
                        onClick = { viewModel.selectAddress(address.id) },
                    ) {
                        Row(
                            modifier = Modifier.padding(Dimensions.paddingMedium),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = if (isSelected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp),
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(
                                        horizontal = Dimensions.paddingSmall
                                    ),
                            ) {
                                Text(
                                    text = address.label,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = "${address.street}, ${address.city}, ${address.state}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = { viewModel.showAddAddressDialog() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimensions.radiusMedium),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.height(0.dp))
                    Text(
                        text = "  Add a delivery address",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            // Payment method section
            item {
                Spacer(modifier = Modifier.height(Dimensions.paddingSmall))
                Text(
                    text = "Payment Method",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall),
                ) {
                    PaymentMethodOption(
                        icon = Icons.Filled.LocalShipping,
                        title = "Cash on Delivery",
                        subtitle = "Pay when your order arrives",
                        isSelected = state.paymentMethod == PAYMENT_METHOD_CASH,
                        onClick = { viewModel.selectPaymentMethod(PAYMENT_METHOD_CASH) },
                    )
                    PaymentMethodOption(
                        icon = Icons.Filled.CreditCard,
                        title = "Card (Demo)",
                        subtitle = if (state.cardNumber.isNotBlank())
                            "•••• ${state.cardNumber.takeLast(4)}"
                        else
                            "Simulated card payment for demo purposes",
                        isSelected = state.paymentMethod == PAYMENT_METHOD_DEMO_CARD,
                        onClick = { viewModel.selectPaymentMethod(PAYMENT_METHOD_DEMO_CARD) },
                        onEditClick = if (state.paymentMethod == PAYMENT_METHOD_DEMO_CARD)
                            { { viewModel.showCardDialog() } }
                        else null,
                    )
                }
            }

            // Order summary section
            item {
                Spacer(modifier = Modifier.height(Dimensions.paddingSmall))
                Text(
                    text = "Order Summary",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimensions.radiusMedium),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = Dimensions.cardElevation
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(Dimensions.paddingMedium),
                    ) {
                        Text(
                            text = "From: ${cartState.sellerName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                        )

                        Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

                        cartState.items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = "${item.quantity}x ${item.product.name}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = "₦${"%.2f".format(item.lineTotal)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Dimensions.paddingSmall))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "Subtotal",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "₦${"%.2f".format(cartState.subtotal)}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "Delivery Fee",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "₦${"%.2f".format(cartState.deliveryFee)}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }

                        Spacer(modifier = Modifier.height(Dimensions.paddingSmall))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "₦${"%.2f".format(cartState.total)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }

    if (state.showAddAddressDialog) {
        var label by remember { mutableStateOf("") }
        var street by remember { mutableStateOf("") }
        var city by remember { mutableStateOf("") }
        var addrState by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { viewModel.hideAddAddressDialog() },
            title = { Text("Add delivery address") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        Dimensions.paddingSmall
                    ),
                ) {
                    OutlinedTextField(
                        value = label,
                        onValueChange = { label = it },
                        label = { Text("Label (e.g. Home)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = street,
                        onValueChange = { street = it },
                        label = { Text("Street") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = addrState,
                        onValueChange = { addrState = it },
                        label = { Text("State") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createAddress(label, street, city, addrState)
                    },
                    enabled = !state.isSavingAddress,
                ) {
                    if (state.isSavingAddress) {
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
                TextButton(onClick = { viewModel.hideAddAddressDialog() }) {
                    Text("Cancel")
                }
            },
        )
    }

    if (state.showCardDialog) {
        CardEntryDialog(
            initialNumber = state.cardNumber,
            onDismiss = {
                viewModel.hideCardDialog()
                if (state.cardNumber.isBlank()) {
                    viewModel.selectPaymentMethod(PAYMENT_METHOD_CASH)
                }
            },
            onSave = { number, month, year, cvv ->
                viewModel.saveCardDetails(number, month, year, cvv)
            },
        )
    }

    if (state.paymentDeclineMessage != null) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Payment Declined") },
            text = {
                Text(
                    "${state.paymentDeclineMessage}\n\nYour order was still placed " +
                        "and will be handled as Cash on Delivery."
                )
            },
            confirmButton = {
                Button(onClick = { viewModel.acknowledgeDecline() }) {
                    Text("OK")
                }
            },
        )
    }
}

@Composable
private fun PaymentMethodOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onEditClick: (() -> Unit)? = null,
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.radiusMedium),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outline,
        ),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(Dimensions.paddingMedium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Dimensions.paddingSmall),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (onEditClick != null) {
                TextButton(onClick = onEditClick) {
                    Text("Edit")
                }
            } else if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun CardEntryDialog(
    initialNumber: String,
    onDismiss: () -> Unit,
    onSave: (number: String, month: Int, year: Int, cvv: String) -> Unit,
) {
    var number by remember { mutableStateOf(initialNumber) }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter demo card details") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall),
            ) {
                Text(
                    text = "This is a demo gateway — no real card data is charged. " +
                        "Try 4242 4242 4242 4242 to succeed, or 4000 0000 0000 0002 " +
                        "to see a decline.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it.filter { c -> c.isDigit() }.take(16) },
                    label = { Text("Card number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall),
                ) {
                    OutlinedTextField(
                        value = expiry,
                        onValueChange = { expiry = it.filter { c -> c.isDigit() || c == '/' }.take(5) },
                        label = { Text("MM/YY") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { cvv = it.filter { c -> c.isDigit() }.take(4) },
                        label = { Text("CVV") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                }
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
                    when {
                        number.length < 12 -> error = "Enter a valid card number."
                        month == null || month !in 1..12 || year == null ->
                            error = "Enter a valid expiry as MM/YY."
                        cvv.length < 3 -> error = "Enter a valid CVV."
                        else -> onSave(number, month, year, cvv)
                    }
                },
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}