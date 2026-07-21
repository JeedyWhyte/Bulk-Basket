package com.bulkbasket.ui.common.navigation

sealed class Routes(val route: String) {
    data object Splash : Routes("splash")
    data object Login : Routes("login")
    data object Signup : Routes("signup")

    // Buyer
    data object BuyerHome : Routes("buyer/home")
    data object Search : Routes("buyer/search")
    data object Cart : Routes("buyer/cart")
    data object Checkout : Routes("buyer/checkout")
    data object BuyerOrders : Routes("buyer/orders")
    data object Tracking : Routes("buyer/tracking/{orderId}")

    // Seller
    data object SellerDashboard : Routes("seller/dashboard")
    data object Inventory : Routes("seller/inventory")
    data object SellerOrders : Routes("seller/orders")

    // Rider
    data object Jobs : Routes("rider/jobs")
    data object ActiveDelivery : Routes("rider/active/{deliveryId}")
    data object Earnings : Routes("rider/earnings")
}