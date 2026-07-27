package com.bulkbasket.ui.buyer

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.bulkbasket.ui.buyer.cart.CartViewModel
import com.bulkbasket.ui.buyer.common.BuyerBottomNav
import com.bulkbasket.ui.buyer.common.BuyerTab
import com.bulkbasket.ui.buyer.home.HomeScreen
import com.bulkbasket.ui.buyer.orders.BuyerOrdersScreen
import com.bulkbasket.ui.buyer.profile.ProfileScreen
import com.bulkbasket.ui.buyer.search.BrowseScreen

@Composable
fun BuyerShellScreen(
    onNavigateToSeller: (Int) -> Unit,
    onNavigateToProduct: (Int) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onLogout: () -> Unit,
    cartViewModel: CartViewModel = hiltViewModel(),
) {
    var activeTab by remember { mutableStateOf<BuyerTab>(BuyerTab.Home) }
    val cartState by cartViewModel.state.collectAsState()

    Scaffold(
        bottomBar = {
            BuyerBottomNav(
                activeTab = activeTab,
                onTabSelected = { activeTab = it },
                orderCount = cartState.itemCount,
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        when (activeTab) {
            BuyerTab.Home -> HomeScreen(
                onSellerClick = onNavigateToSeller,
                onProductClick = onNavigateToProduct,
                onCartClick = onNavigateToCart,
                onOrdersClick = { activeTab = BuyerTab.Orders },
                onProfileClick = { activeTab = BuyerTab.Account },
                onNotificationsClick = onNavigateToNotifications,
                modifier = Modifier.padding(innerPadding),
                cartViewModel = cartViewModel,
            )
            BuyerTab.Browse -> BrowseScreen(
                onProductClick = onNavigateToProduct,
                onCartClick = onNavigateToCart,
                modifier = Modifier.padding(innerPadding),
                cartViewModel = cartViewModel,
            )
            BuyerTab.Orders -> BuyerOrdersScreen(
                onBack = { activeTab = BuyerTab.Home },
                modifier = Modifier.padding(innerPadding),
            )
            BuyerTab.Account -> ProfileScreen(
                onBack = { activeTab = BuyerTab.Home },
                onLogout = onLogout,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
