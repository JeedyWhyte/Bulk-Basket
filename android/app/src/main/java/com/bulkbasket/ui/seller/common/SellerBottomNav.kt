package com.bulkbasket.ui.seller.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

sealed class SellerTab(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    data object Dashboard : SellerTab("seller_tab_dashboard", "Dashboard", Icons.Filled.Dashboard)
    data object Orders    : SellerTab("seller_tab_orders",    "Orders",    Icons.Filled.ShoppingBag)
    data object Inventory : SellerTab("seller_tab_inventory", "Inventory", Icons.Filled.Inventory)
    data object Profile   : SellerTab("seller_tab_profile",   "Profile",   Icons.Filled.Person)
}

val SellerTabs = listOf(
    SellerTab.Dashboard,
    SellerTab.Orders,
    SellerTab.Inventory,
    SellerTab.Profile,
)

@Composable
fun SellerBottomNav(
    activeTab: SellerTab,
    onTabSelected: (SellerTab) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        SellerTabs.forEach { tab ->
            val isSelected = activeTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            )
        }
    }
}
