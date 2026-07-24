package com.bulkbasket.ui.common.navigation

sealed class Routes(val route: String) {

    // Auth
    data object Splash : Routes("splash")
    data object Login : Routes("login")
    data object Signup : Routes("signup")

    // Buyer
    data object BuyerHome : Routes("buyer/home")
    data object Search : Routes("buyer/search")
    data object SellerDetail : Routes("buyer/seller/{sellerId}") {
        fun createRoute(sellerId: Int) = "buyer/seller/$sellerId"
    }
    data object ProductDetail : Routes("buyer/product/{productId}") {
        fun createRoute(productId: Int) = "buyer/product/$productId"
    }
    data object Cart : Routes("buyer/cart")
    data object Checkout : Routes("buyer/checkout")
    data object BuyerOrders : Routes("buyer/orders")
    data object BuyerOrderDetail : Routes("buyer/orders/{orderId}") {
        fun createRoute(orderId: String) = "buyer/orders/$orderId"
    }
    data object Tracking : Routes("buyer/tracking/{orderId}") {
        fun createRoute(orderId: String) = "buyer/tracking/$orderId"
    }

    // Seller
    data object SellerDashboard : Routes("seller/dashboard")
    data object Inventory : Routes("seller/inventory")
    data object AddProduct : Routes("seller/inventory/add")
    data object SellerOrders : Routes("seller/orders")
    data object SellerOrderDetail : Routes("seller/orders/{orderId}") {
        fun createRoute(orderId: String) = "seller/orders/$orderId"
    }
    data object SellerProfile : Routes("seller/profile")

    // Rider
    data object Jobs : Routes("rider/jobs")
    data object ActiveDelivery : Routes("rider/active/{deliveryId}") {
        fun createRoute(deliveryId: Int) = "rider/active/$deliveryId"
    }
    data object Earnings : Routes("rider/earnings")
    data object RiderProfile : Routes("rider/profile")

    // Shared
    data object Notifications : Routes("notifications")
    data object Profile : Routes("profile")
}