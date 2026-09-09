package com.bulkbasket.ui.buyer

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.bulkbasket.ui.buyer.cart.CartScreen
import com.bulkbasket.ui.buyer.cart.CartViewModel
import com.bulkbasket.ui.buyer.common.BuyerBottomNav
import com.bulkbasket.ui.buyer.common.BuyerTab
import com.bulkbasket.ui.buyer.home.HomeScreen
import com.bulkbasket.ui.buyer.orders.BuyerOrdersScreen
import com.bulkbasket.ui.buyer.profile.ProfileScreen
import com.bulkbasket.ui.buyer.profile.ProfileViewModel
import com.bulkbasket.ui.buyer.search.BrowseScreen
import com.bulkbasket.ui.buyer.category.CategoryScreen

@Composable
fun BuyerShellScreen(
    openOrdersTab: Boolean = false,
    onOrdersTabShown: () -> Unit = {},
    onNavigateToSeller: (Int) -> Unit,
    onNavigateToProduct: (Int) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToRatings: () -> Unit = {},
    onNavigateToPaymentSettings: () -> Unit = {},
    onNavigateToAppSettings: () -> Unit = {},
    onNavigateToNotificationPreferences: () -> Unit = {},
    onNavigateToCloseAccount: () -> Unit = {},
    onNavigateToPrivacyPolicy: () -> Unit = {},
    onNavigateToHelpSupport: () -> Unit = {},
    onLogout: () -> Unit,
    cartViewModel: CartViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
) {
    var activeTab by remember { mutableStateOf<BuyerTab>(BuyerTab.Home) }
    val cartState by cartViewModel.state.collectAsState()

    LaunchedEffect(openOrdersTab) {
        if (openOrdersTab) {
            activeTab = BuyerTab.Orders
            onOrdersTabShown()
        }
    }

    Scaffold(
        bottomBar = {
            BuyerBottomNav(
                activeTab = activeTab,
                onTabSelected = { activeTab = it },
                cartCount = cartState.itemCount,
            )
        },
    ) { innerPadding ->
        Crossfade(
            targetState = activeTab,
            label = "buyer_tab",
            animationSpec = tween(durationMillis = 200),
        ) { tab ->
        when (tab) {
            BuyerTab.Home -> HomeScreen(
                onSellerClick = onNavigateToSeller,
                onProductClick = onNavigateToProduct,
                onCartClick = { activeTab = BuyerTab.Cart },
                onOrdersClick = {},
                onProfileClick = { activeTab = BuyerTab.Account },
                onNotificationsClick = onNavigateToNotifications,
                onSearchClick = { activeTab = BuyerTab.Search },
                modifier = Modifier.padding(innerPadding),
                cartViewModel = cartViewModel,
            )
            BuyerTab.Category -> CategoryScreen(
                onProductClick = onNavigateToProduct,
                modifier = Modifier.padding(innerPadding),
                cartViewModel = cartViewModel,
            )
            BuyerTab.Search -> BrowseScreen(
                onProductClick = onNavigateToProduct,
                onSellerClick = onNavigateToSeller,
                onCartClick = { activeTab = BuyerTab.Cart },
                modifier = Modifier.padding(innerPadding),
                cartViewModel = cartViewModel,
            )
            BuyerTab.Cart -> CartScreen(
                onBack = { activeTab = BuyerTab.Home },
                onCheckout = onNavigateToCheckout,
                onOrdersClick = onNavigateToOrders,
                modifier = Modifier.padding(innerPadding),
                viewModel = cartViewModel,
            )
            BuyerTab.Orders -> BuyerOrdersScreen(
                onBack = { activeTab = BuyerTab.Home },
                modifier = Modifier.padding(innerPadding),
            )
            BuyerTab.Account -> ProfileScreen(
                onBack = { activeTab = BuyerTab.Home },
                onLogout = onLogout,
                onOrdersClick = onNavigateToOrders,
                onEditProfileClick = onNavigateToEditProfile,
                onInboxClick = onNavigateToNotifications,
                onRatingsClick = onNavigateToRatings,
                onPaymentSettingsClick = onNavigateToPaymentSettings,
                onAppSettingsClick = onNavigateToAppSettings,
                onNotificationPreferencesClick = onNavigateToNotificationPreferences,
                onCloseAccountClick = onNavigateToCloseAccount,
                onPrivacyPolicyClick = onNavigateToPrivacyPolicy,
                onHelpSupportClick = onNavigateToHelpSupport,
                modifier = Modifier.padding(innerPadding),
                viewModel = profileViewModel,
            )
        }
        }
    }
}
