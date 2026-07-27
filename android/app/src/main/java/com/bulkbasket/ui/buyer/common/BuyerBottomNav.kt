package com.bulkbasket.ui.buyer.common

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.bulkbasket.ui.theme.Primary700
import com.bulkbasket.ui.theme.TextTertiary
import com.bulkbasket.ui.theme.White

sealed class BuyerTab(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    data object Home : BuyerTab("buyer_tab_home", "Home", Icons.Filled.Home)
    data object Browse : BuyerTab("buyer_tab_browse", "Browse", Icons.Filled.Search)
    data object Orders : BuyerTab("buyer_tab_orders", "Orders", Icons.Filled.ShoppingBag)
    data object Account : BuyerTab("buyer_tab_account", "Account", Icons.Filled.Person)
}

val BuyerTabs = listOf(
    BuyerTab.Home,
    BuyerTab.Browse,
    BuyerTab.Orders,
    BuyerTab.Account,
)

@Composable
fun BuyerBottomNav(
    activeTab: BuyerTab,
    onTabSelected: (BuyerTab) -> Unit,
    orderCount: Int = 0,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        tonalElevation = 0.dp,
        modifier = Modifier,
    ) {
        BuyerTabs.forEach { tab ->
            val isSelected = activeTab == tab

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    if (tab == BuyerTab.Orders && orderCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge { Text(orderCount.toString()) }
                            }
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                modifier = Modifier.size(26.dp),
                            )
                        }
                    } else {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            modifier = Modifier.size(26.dp),
                        )
                    }
                },
                label = {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) White
                        else White.copy(alpha = 0.6f),
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary700,
                    unselectedIconColor = White.copy(alpha = 0.6f),
                    selectedTextColor = White,
                    unselectedTextColor = White.copy(alpha = 0.6f),
                    indicatorColor = White,
                ),
            )
        }
    }
}
