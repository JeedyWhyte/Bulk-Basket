package com.bulkbasket.ui.buyer.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bulkbasket.ui.theme.Dimensions
import com.bulkbasket.ui.theme.Green50
import com.bulkbasket.ui.theme.Green600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) onLogout()
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Log Out",
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text("Are you sure you want to log out?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text("Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Profile",
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Log Out",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { innerPadding ->

        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.loadProfile() },
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.user != null -> {
                    val user = state.user!!
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            bottom = Dimensions.paddingLarge
                        ),
                    ) {

                        // Profile header
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer
                                    )
                                    .padding(Dimensions.paddingLarge),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(CircleShape)
                                            .background(Green50),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = user.username?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                            style = MaterialTheme.typography.displayMedium,
                                            color = Green600,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

                                    Text(
                                        text = user.username ?: "Unknown User",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = user.role?.replaceFirstChar {
                                            it.uppercase()
                                        } ?: "Unknown Role",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }
                            }
                        }

                        // Account details
                        item {
                            Spacer(modifier = Modifier.height(Dimensions.paddingLarge))
                            Text(
                                text = "Account Details",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(
                                    horizontal = Dimensions.paddingLarge,
                                    vertical = Dimensions.paddingSmall,
                                ),
                            )
                        }

                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = Dimensions.paddingLarge),
                                shape = RoundedCornerShape(Dimensions.radiusMedium),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = Dimensions.cardElevation
                                ),
                            ) {
                                Column(
                                    modifier = Modifier.padding(
                                        Dimensions.paddingMedium
                                    ),
                                ) {
                                    ProfileInfoRow(
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Filled.Person,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                            )
                                        },
                                        label = "Username",
                                        value = user.username ?: "N/A",
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier.padding(
                                            vertical = Dimensions.paddingSmall
                                        )
                                    )
                                    ProfileInfoRow(
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Filled.Star,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                            )
                                        },
                                        label = "Email",
                                        value = user.email ?: "N/A",
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier.padding(
                                            vertical = Dimensions.paddingSmall
                                        )
                                    )
                                    ProfileInfoRow(
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Filled.Phone,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                            )
                                        },
                                        label = "Phone",
                                        value = user.phoneNumber?.ifBlank {
                                            "Not provided"
                                        } ?: "Not provided",
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier.padding(
                                            vertical = Dimensions.paddingSmall
                                        )
                                    )
                                    ProfileInfoRow(
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Filled.Star,
                                                contentDescription = null,
                                                tint = if (user.isVerified == true)
                                                    MaterialTheme.colorScheme.primary
                                                else
                                                    MaterialTheme.colorScheme.outline,
                                            )
                                        },
                                        label = "Verified",
                                        value = if (user.isVerified == true) "Yes" else "No",
                                    )
                                }
                            }
                        }

                        // Delivery addresses
                        item {
                            Spacer(modifier = Modifier.height(Dimensions.paddingLarge))
                            Text(
                                text = "Delivery Addresses",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(
                                    horizontal = Dimensions.paddingLarge,
                                    vertical = Dimensions.paddingSmall,
                                ),
                            )
                        }

                        if (state.addresses.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = Dimensions.paddingLarge),
                                    shape = RoundedCornerShape(Dimensions.radiusMedium),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    ),
                                ) {
                                    Text(
                                        text = "No delivery addresses added yet.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(
                                            Dimensions.paddingMedium
                                        ),
                                    )
                                }
                            }
                        } else {
                            items(state.addresses) { address ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = Dimensions.paddingLarge,
                                            vertical = Dimensions.paddingXSmall,
                                        ),
                                    shape = RoundedCornerShape(Dimensions.radiusMedium),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = Dimensions.cardElevation
                                    ),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(
                                            Dimensions.paddingMedium
                                        ),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(
                                            Dimensions.paddingSmall
                                        ),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.LocationOn,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(24.dp),
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(
                                                    Dimensions.paddingXSmall
                                                ),
                                            ) {
                                                Text(
                                                    text = address.label,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                )
                                                if (address.isDefault) {
                                                    Text(
                                                        text = "Default",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        fontWeight = FontWeight.Medium,
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "${address.street}, ${address.city}, ${address.state}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Logout button
                        item {
                            Spacer(modifier = Modifier.height(Dimensions.paddingLarge))
                            Button(
                                onClick = { showLogoutDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = Dimensions.paddingLarge)
                                    .height(Dimensions.buttonHeight),
                                shape = RoundedCornerShape(Dimensions.radiusMedium),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                ),
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = null,
                                    modifier = Modifier.padding(
                                        end = Dimensions.paddingSmall
                                    ),
                                )
                                Text(
                                    text = "Log Out",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall),
    ) {
        icon()
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}