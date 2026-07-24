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
import com.bulkbasket.ui.seller.dashboard.SellerDashboardScreen
import com.bulkbasket.ui.rider.jobs.RiderJobsScreen

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
    }
}