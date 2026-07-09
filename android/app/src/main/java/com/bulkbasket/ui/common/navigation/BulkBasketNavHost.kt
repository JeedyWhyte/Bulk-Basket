package com.bulkbasket.ui.common.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bulkbasket.ui.auth.login.LoginScreen
// Import other screens as you build them

@Composable
fun BulkBasketNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Login.route,
    ) {
        composable(Routes.Login.route) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val dest = when (role) {
                        "buyer" -> Routes.BuyerHome.route
                        "seller" -> Routes.SellerDashboard.route
                        "rider" -> Routes.Jobs.route
                        else -> Routes.BuyerHome.route
                    }
                    navController.navigate(dest) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignup = {
                    navController.navigate(Routes.Signup.route)
                },
            )
        }

        // Add more composable() blocks as you build each screen
        // composable(Routes.BuyerHome.route) { HomeScreen(...) }
        // composable(Routes.SellerDashboard.route) { SellerDashboardScreen(...) }
    }
}