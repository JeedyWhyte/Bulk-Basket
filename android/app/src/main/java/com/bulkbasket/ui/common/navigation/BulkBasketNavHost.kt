package com.bulkbasket.ui.common.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.bulkbasket.ui.buyer.profile.ProfileViewModel
import com.bulkbasket.ui.buyer.profile.EditProfileScreen
import com.bulkbasket.ui.buyer.profile.RatingsReviewsScreen
import com.bulkbasket.ui.buyer.profile.PaymentSettingsScreen
import com.bulkbasket.ui.buyer.profile.AppSettingsScreen
import com.bulkbasket.ui.buyer.profile.NotificationPreferencesScreen
import com.bulkbasket.ui.buyer.profile.CloseAccountScreen
import com.bulkbasket.ui.buyer.profile.PrivacyPolicyScreen
import com.bulkbasket.ui.buyer.profile.HelpSupportScreen
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

/**
 * Resolves the buyer graph's back-stack entry so the Account tab and every
 * Account sub-page (Edit Profile, etc.) share one [ProfileViewModel] —
 * saving a profile edit is reflected immediately when navigating back.
 */
@Composable
private fun buyerGraphProfileViewModel(
    navController: NavHostController,
    entry: NavBackStackEntry,
): ProfileViewModel {
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
        enterTransition = { fadeIn(animationSpec = tween(220)) },
        exitTransition = { fadeOut(animationSpec = tween(220)) },
        popEnterTransition = { fadeIn(animationSpec = tween(220)) },
        popExitTransition = { fadeOut(animationSpec = tween(220)) },
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
                val profileViewModel = buyerGraphProfileViewModel(navController, it)
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
                    onNavigateToEditProfile = {
                        navController.navigate(Routes.EditProfile.route)
                    },
                    onNavigateToRatings = {
                        navController.navigate(Routes.RatingsReviews.route)
                    },
                    onNavigateToPaymentSettings = {
                        navController.navigate(Routes.PaymentSettings.route)
                    },
                    onNavigateToAppSettings = {
                        navController.navigate(Routes.AppSettings.route)
                    },
                    onNavigateToNotificationPreferences = {
                        navController.navigate(Routes.NotificationPreferences.route)
                    },
                    onNavigateToCloseAccount = {
                        navController.navigate(Routes.CloseAccount.route)
                    },
                    onNavigateToPrivacyPolicy = {
                        navController.navigate(Routes.PrivacyPolicy.route)
                    },
                    onNavigateToHelpSupport = {
                        navController.navigate(Routes.HelpSupport.route)
                    },
                    onLogout = {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    cartViewModel = cartViewModel,
                    profileViewModel = profileViewModel,
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

            composable(Routes.EditProfile.route) {
                val profileViewModel = buyerGraphProfileViewModel(navController, it)

                EditProfileScreen(
                    onBack = { navController.popBackStack() },
                    viewModel = profileViewModel,
                )
            }

            composable(Routes.RatingsReviews.route) {
                RatingsReviewsScreen(
                    onBack = { navController.popBackStack() },
                )
            }

            composable(Routes.PaymentSettings.route) {
                PaymentSettingsScreen(
                    onBack = { navController.popBackStack() },
                )
            }

            composable(Routes.AppSettings.route) {
                AppSettingsScreen(
                    onBack = { navController.popBackStack() },
                )
            }

            composable(Routes.NotificationPreferences.route) {
                NotificationPreferencesScreen(
                    onBack = { navController.popBackStack() },
                )
            }

            composable(Routes.CloseAccount.route) {
                CloseAccountScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToHelp = {
                        navController.navigate(Routes.HelpSupport.route)
                    },
                    onAccountClosed = {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                )
            }

            composable(Routes.PrivacyPolicy.route) {
                PrivacyPolicyScreen(
                    onBack = { navController.popBackStack() },
                )
            }

            composable(Routes.HelpSupport.route) {
                HelpSupportScreen(
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
