package com.bulkbasket.ui.buyer.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.IconButton
import com.bulkbasket.ui.buyer.cart.CartViewModel
import com.bulkbasket.ui.buyer.common.ProductCard
import com.bulkbasket.ui.buyer.common.SellerCard
import com.bulkbasket.ui.theme.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSellerClick: (Int) -> Unit,
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val cartState by cartViewModel.state.collectAsState()
    val isRefreshing = state.isLoadingSellers || state.isLoadingProducts

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "BulkBasket",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Fresh from the market",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                    IconButton(onClick = onOrdersClick) {
                        Icon(
                            imageVector = Icons.Filled.Receipt,
                            contentDescription = "My Orders",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                    BadgedBox(
                        badge = {
                            if (cartState.itemCount > 0) {
                                Badge { Text(cartState.itemCount.toString()) }
                            }
                        },
                    ) {
                        IconButton(onClick = onCartClick) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = "Cart",
                                tint = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.loadHomeData() },
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = Dimensions.paddingLarge),
            ) {

                // Nearby Sellers Section
                item {
                    Text(
                        text = "Nearby Markets",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            start = Dimensions.paddingLarge,
                            top = Dimensions.paddingLarge,
                            bottom = Dimensions.paddingSmall,
                        ),
                    )
                }

                item {
                    if (state.isLoadingSellers) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    } else if (state.nearbySellers.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
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
                            horizontalArrangement = Arrangement.spacedBy(
                                Dimensions.paddingMedium
                            ),
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

                // Featured Products Section
                item {
                    Spacer(modifier = Modifier.height(Dimensions.paddingLarge))
                    Text(
                        text = "Available Products",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = Dimensions.paddingLarge,
                            vertical = Dimensions.paddingSmall,
                        ),
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
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                } else if (state.featuredProducts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
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
                    items(state.featuredProducts) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) },
                            modifier = Modifier.padding(
                                horizontal = Dimensions.paddingLarge,
                                vertical = Dimensions.paddingXSmall,
                            ),
                        )
                    }
                }
            }
        }
    }
}