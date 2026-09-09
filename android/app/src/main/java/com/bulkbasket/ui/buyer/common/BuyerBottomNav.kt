package com.bulkbasket.ui.buyer.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

sealed class BuyerTab(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    data object Home     : BuyerTab("buyer_tab_home",     "Home",     Icons.Filled.Home)
    data object Category : BuyerTab("buyer_tab_category", "Category", Icons.Filled.Category)
    data object Search   : BuyerTab("buyer_tab_search",   "Search",   Icons.Filled.Search)
    data object Cart     : BuyerTab("buyer_tab_cart",     "Cart",     Icons.Filled.ShoppingCart)
    data object Orders   : BuyerTab("buyer_tab_orders",   "Orders",   Icons.Filled.Receipt)
    data object Account  : BuyerTab("buyer_tab_account",  "Account",  Icons.Filled.Person)
}

val BuyerTabs = listOf(
    BuyerTab.Home,
    BuyerTab.Category,
    BuyerTab.Search,
    BuyerTab.Cart,
    BuyerTab.Account,
)

@Composable
fun BuyerBottomNav(
    activeTab: BuyerTab,
    onTabSelected: (BuyerTab) -> Unit,
    cartCount: Int = 0,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        BuyerTabs.forEach { tab ->
            val isSelected = activeTab == tab

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    if (tab == BuyerTab.Cart && cartCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                ) {
                                    Text(cartCount.toString())
                                }
                            }
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                            )
                        }
                    } else {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                        )
                    }
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
