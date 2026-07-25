package com.bulkbasket.ui.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bulkbasket.ui.auth.login.LoginScreen
import com.bulkbasket.ui.auth.signup.SignupScreen
import com.bulkbasket.ui.splash.SplashScreen
import com.bulkbasket.ui.buyer.home.HomeScreen
import com.bulkbasket.ui.buyer.sellerdetail.SellerDetailScreen
import com.bulkbasket.ui.buyer.cart.CartScreen
import com.bulkbasket.ui.buyer.checkout.CheckoutScreen
import com.bulkbasket.ui.buyer.orders.BuyerOrdersScreen
import com.bulkbasket.ui.buyer.profile.ProfileScreen
import com.bulkbasket.ui.seller.dashboard.SellerDashboardScreen
import com.bulkbasket.ui.rider.jobs.RiderJobsScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text

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
            HomeScreen(
                onSellerClick = { sellerId ->
                    navController.navigate(
                        Routes.SellerDetail.createRoute(sellerId)
                    )
                },
                onProductClick = { productId ->
                    navController.navigate(
                        Routes.ProductDetail.createRoute(productId)
                    )
                },
                onCartClick = {
                    navController.navigate(Routes.Cart.route)
                },
                onOrdersClick = {
                    navController.navigate(Routes.BuyerOrders.route)
                },
                onProfileClick = {
                    navController.navigate(Routes.Profile.route)
                },
            )
        }

        // Seller screens
        composable(Routes.SellerDashboard.route) {
            SellerDashboardScreen()
        }

        // Rider screens
        composable(Routes.Jobs.route) {
            RiderJobsScreen()
        }

        // Seller Details
        composable(Routes.SellerDetail.route) {
            SellerDetailScreen(
                onBack = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate(
                        Routes.ProductDetail.createRoute(productId)
                    )
                },
                onViewCart = {
                    navController.navigate(Routes.Cart.route)
                },
            )
        }

        composable(Routes.Cart.route) {
            CartScreen(
                onBack = { navController.popBackStack() },
                onCheckout = {
                    navController.navigate(Routes.Checkout.route)
                },
            )
        }

        composable(Routes.Checkout.route) {
            CheckoutScreen(
                onBack = { navController.popBackStack() },
                onOrderSuccess = {
                    navController.navigate(Routes.BuyerOrders.route) {
                        popUpTo(Routes.BuyerHome.route)
                    }
                },
            )
        }

        composable(Routes.ProductDetail.route) {
            // Placeholder — will be built out later
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Product Detail — Coming Soon")
            }
        }

        composable(Routes.BuyerOrders.route) {
            BuyerOrdersScreen(
                onBack = { navController.popBackStack() },
            )
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