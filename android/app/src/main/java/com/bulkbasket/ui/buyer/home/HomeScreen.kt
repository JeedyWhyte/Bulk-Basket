package com.bulkbasket.ui.buyer.home

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bulkbasket.domain.model.Seller
import com.bulkbasket.ui.buyer.cart.CartViewModel
import com.bulkbasket.ui.buyer.common.ProductCard
import com.bulkbasket.ui.theme.Accent500
import com.bulkbasket.ui.theme.Dimensions
import com.bulkbasket.ui.theme.Gray100
import com.bulkbasket.ui.theme.Primary50
import com.bulkbasket.ui.theme.Primary700
import com.bulkbasket.ui.theme.SuccessDark
import com.bulkbasket.ui.theme.SuccessLight
import com.bulkbasket.ui.theme.TextPrimary
import com.bulkbasket.ui.theme.TextTertiary
import java.util.Calendar

private data class Category(val name: String, val emoji: String)

private val categories = listOf(
    Category("Grains", "🌾"),
    Category("Produce", "🌿"),
    Category("Oils", "🫙"),
    Category("Spices", "⭐"),
    Category("Protein", "🐟"),
    Category("Bakery", "🥐"),
)

private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else      -> "Good evening"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSellerClick: (Int) -> Unit,
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val cartState by cartViewModel.state.collectAsState()
    val isRefreshing = state.isLoadingSellers || state.isLoadingProducts
    val greeting = remember { getGreeting() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = greeting,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        )
                        Text(
                            text = state.username.ifBlank { "Welcome" },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
                actions = {
                    // Notification bell with circle background
                    Box(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)
                            )
                            .clickable { onNotificationsClick() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp),
                        )
                    }

                    // Cart with circle background and badge
                    BadgedBox(
                        badge = {
                            if (cartState.itemCount > 0) {
                                Badge(containerColor = Accent500) {
                                    Text(
                                        text = cartState.itemCount.toString(),
                                        color = TextPrimary,
                                        fontSize = 9.sp,
                                    )
                                }
                            }
                        },
                        modifier = Modifier.padding(end = 12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)
                                )
                                .clickable { onCartClick() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = "Cart",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    scrolledContainerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = modifier,
    ) { innerPadding ->

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.loadHomeData() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
            ) {
                // Browse categories
                item {
                    Text(
                        text = "Browse categories",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(horizontal = Dimensions.paddingLarge)
                            .padding(top = 4.dp, bottom = 12.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(
                            horizontal = Dimensions.paddingLarge
                        ),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(bottom = 20.dp),
                    ) {
                        items(categories) { category ->
                            CategoryTile(category = category)
                        }
                    }
                }

                // Nearby sellers
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimensions.paddingLarge)
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Nearby sellers",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = "See all",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {},
                        )
                    }
                }

                item {
                    if (state.isLoadingSellers) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else if (state.nearbySellers.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimensions.paddingLarge)
                                .padding(bottom = 20.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No markets found nearby",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        LazyRow(
                            contentPadding = PaddingValues(
                                horizontal = Dimensions.paddingLarge
                            ),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(bottom = 20.dp),
                        ) {
                            items(state.nearbySellers) { seller ->
                                SellerCard(
                                    seller = seller,
                                    onClick = { onSellerClick(seller.id) },
                                )
                            }
                        }
                    }
                }

                // Popular products
                item {
                    Text(
                        text = "Popular this week",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(horizontal = Dimensions.paddingLarge)
                            .padding(bottom = 12.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }

                if (state.isLoadingProducts) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                } else if (state.featuredProducts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimensions.paddingLarge),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "No products available yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                } else {
                    items(state.featuredProducts.chunked(2)) { pair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimensions.paddingLarge)
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            pair.forEach { product ->
                                ProductCard(
                                    product = product,
                                    onClick = { onProductClick(product.id) },
                                    onAddToCart = {
                                        cartViewModel.addToCart(
                                            product = product,
                                            sellerId = product.sellerId,
                                            sellerName = product.sellerName,
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryTile(category: Category) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(60.dp),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable {},
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = category.emoji,
                fontSize = 24.sp,
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SellerCard(
    seller: Seller,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(Dimensions.radiusMedium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Dimensions.cardElevation
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Primary50),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Store,
                        contentDescription = null,
                        tint = Primary700,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = seller.businessName,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = seller.marketName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Accent500,
                        modifier = Modifier.size(12.dp),
                    )
                    Text(
                        text = seller.rating,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (seller.isOpen) SuccessLight else Gray100
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = if (seller.isOpen) "Open" else "Closed",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = if (seller.isOpen) SuccessDark else TextTertiary,
                    )
                }
            }
        }
    }
}
