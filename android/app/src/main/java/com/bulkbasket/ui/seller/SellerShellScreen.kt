package com.bulkbasket.ui.seller

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.bulkbasket.ui.seller.common.SellerBottomNav
import com.bulkbasket.ui.seller.common.SellerTab
import com.bulkbasket.ui.seller.dashboard.SellerDashboardScreen
import com.bulkbasket.ui.seller.inventory.InventoryScreen
import com.bulkbasket.ui.seller.orders.SellerOrdersScreen
import com.bulkbasket.ui.seller.profile.SellerProfileScreen

@Composable
fun SellerShellScreen(
    onNavigateToNotifications: () -> Unit,
    onLogout: () -> Unit,
) {
    var activeTab by remember { mutableStateOf<SellerTab>(SellerTab.Dashboard) }

    Scaffold(
        bottomBar = {
            SellerBottomNav(
                activeTab = activeTab,
                onTabSelected = { activeTab = it },
            )
        },
    ) { innerPadding ->
        when (activeTab) {
            SellerTab.Dashboard -> SellerDashboardScreen(
                onNotificationsClick = onNavigateToNotifications,
                onOrdersClick = { activeTab = SellerTab.Orders },
                modifier = Modifier.padding(innerPadding),
            )
            SellerTab.Orders -> SellerOrdersScreen(
                modifier = Modifier.padding(innerPadding),
            )
            SellerTab.Inventory -> InventoryScreen(
                onBack = { activeTab = SellerTab.Dashboard },
                modifier = Modifier.padding(innerPadding),
            )
            SellerTab.Profile -> SellerProfileScreen(
                onBack = { activeTab = SellerTab.Dashboard },
                onLogout = onLogout,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
