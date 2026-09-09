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
import com.bulkbasket.ui.buyer.sellerdetail.SellerDetailScreen
import com.bulkbasket.ui.buyer.cart.CartScreen
import com.bulkbasket.ui.buyer.checkout.CheckoutScreen
import com.bulkbasket.ui.buyer.orders.BuyerOrdersScreen
import com.bulkbasket.ui.buyer.profile.ProfileScreen
import com.bulkbasket.ui.buyer.BuyerShellScreen
import com.bulkbasket.ui.seller.SellerShellScreen
import com.bulkbasket.ui.seller.inventory.InventoryScreen
import com.bulkbasket.ui.seller.profile.SellerProfileScreen
import com.bulkbasket.ui.rider.jobs.RiderJobsScreen
import com.bulkbasket.ui.rider.profile.RiderProfileScreen
import com.bulkbasket.ui.rider.activedelivery.ActiveDeliveryScreen
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.bulkbasket.ui.buyer.cart.CartViewModel

/** Route of the nested graph that owns the shared buyer [CartViewModel]. */
private const val BUYER_GRAPH_ROUTE = "buyer_graph"

/** SavedStateHandle key: tells the buyer shell to open the Orders tab. */
private const val KEY_OPEN_ORDERS_TAB = "open_orders_tab"

/**
 * Resolves the buyer graph's back-stack entry so every buyer screen shares
 * one [CartViewModel]. Unlike resolving against the BuyerHome destination,
 * the graph entry always exists while any buyer screen is on the stack —
 * including after process-death restoration.
 */
@Composable
private fun buyerGraphCartViewModel(
    navController: NavHostController,
    entry: NavBackStackEntry,
): CartViewModel {
    val parentEntry = remember(entry) {
        navController.getBackStackEntry(BUYER_GRAPH_ROUTE)
    }
    return hiltViewModel(parentEntry)
}

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

        // Buyer screens — nested graph so all of them share one CartViewModel
        navigation(
            route = BUYER_GRAPH_ROUTE,
            startDestination = Routes.BuyerHome.route,
        ) {
            composable(Routes.BuyerHome.route) {
                val cartViewModel: CartViewModel = hiltViewModel(it)
                BuyerShellScreen(
                    onNavigateToSeller = { sellerId ->
                        navController.navigate(Routes.SellerDetail.createRoute(sellerId))
                    },
                    onNavigateToProduct = { productId ->
                        navController.navigate(Routes.ProductDetail.createRoute(productId))
                    },
                    onNavigateToCart = {
                        navController.navigate(Routes.Cart.route)
                    },
                    onNavigateToNotifications = {
                        navController.navigate(Routes.Notifications.route)
                    },
                    onNavigateToCheckout = {
                        navController.navigate(Routes.Checkout.route)
                    },
                    onNavigateToOrders = {
                        navController.navigate(Routes.BuyerOrders.route)
                    },
                    onLogout = {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    cartViewModel = cartViewModel,
                )
            }

            // Seller Details
            composable(
                route = Routes.SellerDetail.route,
                arguments = listOf(
                    navArgument("sellerId") { type = NavType.IntType }
                ),
            ) {
                val cartViewModel = buyerGraphCartViewModel(navController, it)

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
                val cartViewModel = buyerGraphCartViewModel(navController, it)

                CartScreen(
                    onBack = { navController.popBackStack() },
                    onCheckout = { navController.navigate(Routes.Checkout.route) },
                    onOrdersClick = { navController.navigate(Routes.BuyerOrders.route) },
                    viewModel = cartViewModel,
                )
            }

            composable(Routes.Checkout.route) {
                val cartViewModel = buyerGraphCartViewModel(navController, it)

                CheckoutScreen(
                    onBack = { navController.popBackStack() },
                    onOrderSuccess = {
                        // Return to the buyer shell (which owns the bottom
                        // nav) and ask it to show the Orders tab, instead of
                        // pushing the bare BuyerOrders route that has no nav.
                        navController
                            .getBackStackEntry(Routes.BuyerHome.route)
                            .savedStateHandle[KEY_OPEN_ORDERS_TAB] = true
                        navController.popBackStack(
                            Routes.BuyerHome.route,
                            inclusive = false,
                        )
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
                val cartViewModel = buyerGraphCartViewModel(navController, it)

                ProductDetailScreen(
                    onBack = { navController.popBackStack() },
                    onViewCart = { navController.navigate(Routes.Cart.route) },
                    cartViewModel = cartViewModel,
                )
            }

            composable(Routes.BuyerOrders.route) {
                BuyerOrdersScreen(
                    onBack = { navController.popBackStack() },
                )
            }
        }

        // Seller screens
        composable(Routes.SellerDashboard.route) {
            SellerShellScreen(
                onNavigateToNotifications = {
                    navController.navigate(Routes.Notifications.route)
                },
                onLogout = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
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

        composable(Routes.Notifications.route) {
            NotificationsScreen(
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
