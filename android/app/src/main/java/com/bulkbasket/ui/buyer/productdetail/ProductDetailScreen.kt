package com.bulkbasket.ui.buyer.productdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.bulkbasket.ui.buyer.cart.CartViewModel
import com.bulkbasket.ui.theme.Accent500
import com.bulkbasket.ui.theme.Dimensions

@Composable
fun ProductDetailScreen(
    onBack: () -> Unit,
    onViewCart: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductDetailViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var quantity by remember { mutableIntStateOf(1) }

    LaunchedEffect(state.addedToCart) {
        if (state.addedToCart) {
            snackbarHostState.showSnackbar("Added to cart ✓")
            viewModel.clearAddedToCart()
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(snackbarData = data)
            }
        },
        bottomBar = {
            if (state.product != null && state.product!!.inStock) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(
                                horizontal = Dimensions.paddingLarge,
                                vertical = Dimensions.paddingMedium,
                            ),
                        horizontalArrangement = Arrangement.spacedBy(
                            Dimensions.paddingMedium
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Quantity selector
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(Dimensions.radiusFull))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(
                                    horizontal = Dimensions.paddingSmall,
                                    vertical = 4.dp,
                                ),
                        ) {
                            IconButton(
                                onClick = {
                                    if (quantity > 1) quantity--
                                },
                                modifier = Modifier.size(32.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Remove,
                                    contentDescription = "Decrease",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 4.dp),
                            )
                            IconButton(
                                onClick = { quantity++ },
                                modifier = Modifier.size(32.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Increase",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }

                        // Add to Cart button
                        Button(
                            onClick = {
                                state.product?.let { product ->
                                    repeat(quantity) {
                                        cartViewModel.addToCart(
                                            product = product,
                                            sellerId = product.sellerId,
                                            sellerName = product.sellerName,
                                        )
                                    }
                                    viewModel.onAddedToCart()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(Dimensions.buttonHeight),
                            shape = RoundedCornerShape(Dimensions.radiusFull),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            ),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(end = 4.dp),
                            )
                            Text(
                                text = "Add to Cart",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        },
    ) { innerPadding ->

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }

            state.product != null -> {
                val product = state.product!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                ) {

                    // ── Full width image with floating buttons ────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                    ) {
                        // Product image
                        if (product.imageUrl != null) {
                            AsyncImage(
                                model = product.imageUrl,
                                contentDescription = product.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = product.name.first()
                                        .uppercaseChar()
                                        .toString(),
                                    style = MaterialTheme.typography.displayLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        // Floating back button
                        Box(
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(Dimensions.paddingMedium)
                                .align(Alignment.TopStart)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f))
                                .clickable { onBack() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp),
                            )
                        }

                        // Floating favourite button
                        Box(
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(Dimensions.paddingMedium)
                                .align(Alignment.TopEnd)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f))
                                .clickable { },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FavoriteBorder,
                                contentDescription = "Favourite",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp),
                            )
                        }

                        // Discount badge if price is good value
                        if (product.inStock) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .statusBarsPadding()
                                    .padding(
                                        top = 56.dp,
                                        end = Dimensions.paddingMedium,
                                    )
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = "In Stock",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }

                    // ── Scrollable product details ────────────────
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(
                                    topStart = Dimensions.radiusLarge,
                                    topEnd = Dimensions.radiusLarge,
                                )
                            )
                            .background(MaterialTheme.colorScheme.background)
                            .padding(Dimensions.paddingLarge),
                    ) {

                        // Category tag
                        if (product.categoryName != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(Dimensions.radiusFull))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(
                                        horizontal = Dimensions.paddingMedium,
                                        vertical = 4.dp,
                                    ),
                            ) {
                                Text(
                                    text = product.categoryName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))
                        }

                        // Name
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )

                        Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

                        // Rating row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = Accent500,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                            Text(
                                text = "4.8",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = "(${product.stockQuantity} reviews)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

                        // Price row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(
                                    text = "₦${product.price}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Text(
                                    text = "per ${product.unit}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Dimensions.paddingMedium))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

                        // Attribute chips
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(
                                Dimensions.paddingSmall
                            ),
                        ) {
                            AttributeChip(
                                icon = "🌿",
                                label = "Fresh",
                            )
                            AttributeChip(
                                icon = "🚚",
                                label = "Free delivery",
                            )
                            AttributeChip(
                                icon = "📦",
                                label = "Bulk",
                            )
                            AttributeChip(
                                icon = "✅",
                                label = "Verified",
                            )
                        }

                        Spacer(modifier = Modifier.height(Dimensions.paddingMedium))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

                        // Description
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Spacer(modifier = Modifier.height(Dimensions.paddingSmall))
                        Text(
                            text = product.description.ifBlank {
                                "Fresh ${product.name} available in bulk. " +
                                    "Sourced directly from local markets for the " +
                                    "best quality and price. Minimum order: " +
                                    "${product.minOrderQty} ${product.unit}."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp,
                        )

                        Spacer(modifier = Modifier.height(Dimensions.paddingMedium))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

                        // Product details
                        Text(
                            text = "Product Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

                        ProductDetailRow(
                            label = "Sold by",
                            value = product.sellerName,
                        )
                        ProductDetailRow(
                            label = "Unit",
                            value = product.unit,
                        )
                        ProductDetailRow(
                            label = "Minimum order",
                            value = "${product.minOrderQty} ${product.unit}",
                        )
                        ProductDetailRow(
                            label = "Available stock",
                            value = "${product.stockQuantity} ${product.unit}",
                        )
                        if (product.categoryName != null) {
                            ProductDetailRow(
                                label = "Category",
                                value = product.categoryName,
                            )
                        }

                        // Bottom padding so content clears the bottom bar
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AttributeChip(
    icon: String,
    label: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(Dimensions.radiusMedium))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(
                horizontal = Dimensions.paddingSmall,
                vertical = Dimensions.paddingSmall,
            ),
    ) {
        Text(
            text = icon,
            fontSize = 20.sp,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ProductDetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
