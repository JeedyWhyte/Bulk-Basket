package com.bulkbasket.ui.buyer.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bulkbasket.ui.buyer.cart.CartViewModel
import com.bulkbasket.ui.buyer.common.ProductCard
import com.bulkbasket.ui.buyer.home.HomeViewModel
import com.bulkbasket.ui.theme.Gray100
import com.bulkbasket.ui.theme.Gray200
import com.bulkbasket.ui.theme.Primary700
import com.bulkbasket.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    onProductClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("All", "Grains", "Produce", "Oils", "Spices", "Protein", "Bakery")
    var selectedCategory by remember { mutableStateOf("All") }

    val filtered = state.featuredProducts.filter { product ->
        (searchQuery.isEmpty() || product.name.contains(searchQuery, ignoreCase = true)) &&
            (selectedCategory == "All" || product.categoryName == selectedCategory)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Browse Essentials",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // Search bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search products...",
                            color = TextTertiary,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            tint = TextTertiary,
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Gray100,
                        focusedContainerColor = Gray100,
                        unfocusedBorderColor = Gray200,
                    ),
                    singleLine = true,
                )
                IconButton(
                    onClick = {},
                    modifier = Modifier.size(52.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = "Filter",
                        tint = Primary700,
                    )
                }
            }

            // Category chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary700,
                            selectedLabelColor = Color.White,
                        ),
                    )
                }
            }

            // Products grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 16.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(filtered) { product ->
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
                    )
                }
            }
        }
    }
}
