package com.bulkbasket.ui.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bulkbasket.ui.auth.login.LoginScreen
import com.bulkbasket.ui.auth.signup.SignupScreen
import com.bulkbasket.ui.splash.SplashScreen
import com.bulkbasket.ui.shared.notifications.NotificationsScreen
import com.bulkbasket.ui.buyer.productdetail.ProductDetailScreen
import com.bulkbasket.ui.buyer.home.HomeScreen
import com.bulkbasket.ui.buyer.sellerdetail.SellerDetailScreen
import com.bulkbasket.ui.buyer.cart.CartScreen
import com.bulkbasket.ui.buyer.checkout.CheckoutScreen
import com.bulkbasket.ui.buyer.orders.BuyerOrdersScreen
import com.bulkbasket.ui.buyer.profile.ProfileScreen
import com.bulkbasket.ui.seller.dashboard.SellerDashboardScreen
import com.bulkbasket.ui.seller.inventory.InventoryScreen
import com.bulkbasket.ui.seller.profile.SellerProfileScreen
import com.bulkbasket.ui.rider.jobs.RiderJobsScreen
import com.bulkbasket.ui.rider.profile.RiderProfileScreen
import com.bulkbasket.ui.rider.activedelivery.ActiveDeliveryScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.bulkbasket.ui.buyer.cart.CartViewModel

@Composable
fun BulkBasketNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route,
        modifier = modifier,
    ) {
        // Splash
        composable(Routes.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToBuyerHome = {
                    navController.navigate(Routes.BuyerHome.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToSellerDashboard = {
                    navController.navigate(Routes.SellerDashboard.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToRiderJobs = {
                    navController.navigate(Routes.Jobs.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                },
            )
        }

        // Auth
        composable(Routes.Login.route) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val destination = when (role) {
                        "buyer"  -> Routes.BuyerHome.route
                        "seller" -> Routes.SellerDashboard.route
                        "rider"  -> Routes.Jobs.route
                        else     -> Routes.BuyerHome.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignup = {
                    navController.navigate(Routes.Signup.route)
                },
            )
        }

        composable(Routes.Signup.route) {
            SignupScreen(
                onSignupSuccess = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Signup.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
            )
        }

        // Buyer screens
        composable(Routes.BuyerHome.route) {
            val cartViewModel: CartViewModel = hiltViewModel(it)

            HomeScreen(
                onSellerClick = { sellerId ->
                    navController.navigate(Routes.SellerDetail.createRoute(sellerId))
                },
                onProductClick = { productId ->
                    navController.navigate(Routes.ProductDetail.createRoute(productId))
                },
                onCartClick = { navController.navigate(Routes.Cart.route) },
                onOrdersClick = { navController.navigate(Routes.BuyerOrders.route) },
                onProfileClick = { navController.navigate(Routes.Profile.route) },
                onNotificationsClick = {
                    navController.navigate(Routes.Notifications.route)
                },
                cartViewModel = cartViewModel,
            )
        }

        // Seller screens
        composable(Routes.SellerDashboard.route) {
            SellerDashboardScreen(
                onInventoryClick = {
                    navController.navigate(Routes.Inventory.route)
                },
                onProfileClick = {
                    navController.navigate(Routes.SellerProfile.route)
                },
                onNotificationsClick = {
                    navController.navigate(Routes.Notifications.route)
                },
            )
        }

        composable(Routes.Inventory.route) {
            InventoryScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.SellerProfile.route) {
            SellerProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        // Rider screens
        composable(Routes.Jobs.route) {
            RiderJobsScreen(
                onProfileClick = {
                    navController.navigate(Routes.RiderProfile.route)
                },
                onActiveDeliveryClick = { deliveryId ->
                    navController.navigate(
                        Routes.ActiveDelivery.createRoute(deliveryId)
                    )
                },
                onNotificationsClick = {
                    navController.navigate(Routes.Notifications.route)
                                       },
            )
        }

        composable(Routes.RiderProfile.route) {
            RiderProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        composable(
            route = Routes.ActiveDelivery.route,
            arguments = listOf(
                navArgument("deliveryId") { type = NavType.IntType }
            ),
        ) {
            ActiveDeliveryScreen(
                onBack = { navController.popBackStack() },
                onDeliveryComplete = {
                    navController.navigate(Routes.Jobs.route) {
                        popUpTo(Routes.Jobs.route) { inclusive = true }
                    }
                },
            )
        }

        // Seller Details
        composable(
            route = Routes.SellerDetail.route,
            arguments = listOf(
                navArgument("sellerId") { type = NavType.IntType }
            ),
        ) {
            val buyerHomeEntry = remember(it) {
                navController.getBackStackEntry(Routes.BuyerHome.route)
            }
            val cartViewModel: CartViewModel = hiltViewModel(buyerHomeEntry)

            SellerDetailScreen(
                onBack = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate(Routes.ProductDetail.createRoute(productId))
                },
                onViewCart = { navController.navigate(Routes.Cart.route) },
                cartViewModel = cartViewModel,
            )
        }

        composable(Routes.Cart.route) {
            val buyerHomeEntry = remember(it) {
                navController.getBackStackEntry(Routes.BuyerHome.route)
            }
            val cartViewModel: CartViewModel = hiltViewModel(buyerHomeEntry)

            CartScreen(
                onBack = { navController.popBackStack() },
                onCheckout = { navController.navigate(Routes.Checkout.route) },
                viewModel = cartViewModel,
            )
        }

        composable(Routes.Checkout.route) {
            val buyerHomeEntry = remember(it) {
                navController.getBackStackEntry(Routes.BuyerHome.route)
            }
            val cartViewModel: CartViewModel = hiltViewModel(buyerHomeEntry)

            CheckoutScreen(
                onBack = { navController.popBackStack() },
                onOrderSuccess = {
                    navController.navigate(Routes.BuyerOrders.route) {
                        popUpTo(Routes.BuyerHome.route)
                    }
                },
                cartViewModel = cartViewModel,
            )
        }

        composable(
            route = Routes.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.IntType }
            ),
        ) {
            val buyerHomeEntry = remember(it) {
                navController.getBackStackEntry(Routes.BuyerHome.route)
            }
            val cartViewModel: CartViewModel = hiltViewModel(buyerHomeEntry)

            ProductDetailScreen(
                onBack = { navController.popBackStack() },
                onViewCart = { navController.navigate(Routes.Cart.route) },
                cartViewModel = cartViewModel,
            )
        }

        composable(Routes.Notifications.route) {
            NotificationsScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.BuyerOrders.route) {
            BuyerOrdersScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.BuyerOrderDetail.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            ),
        ) {
            // placeholder for now
        }

        composable(
            route = Routes.Tracking.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            ),
        ) {
            // placeholder for now
        }

        composable(Routes.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
    }
}