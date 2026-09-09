package com.bulkbasket.ui.seller.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.Surface
import com.bulkbasket.ui.theme.ThemeMode
import com.bulkbasket.ui.theme.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SellerProfileViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) onLogout()
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    ),
                ) { Text("Log Out") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    if (state.showEditDialog) {
        SellerProfileDialog(
            seller = state.seller,
            onDismiss = { viewModel.hideEditDialog() },
            onSave = { businessName, marketName, description, openingTime, closingTime ->
                viewModel.saveProfile(
                    businessName = businessName,
                    marketName = marketName,
                    description = description,
                    openingTime = openingTime,
                    closingTime = closingTime,
                )
            },
        )
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(
                        horizontal = Dimensions.paddingLarge,
                        vertical = 4.dp,
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "My Profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    IconButton(onClick = { viewModel.showEditDialog() }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(snackbarData = data)
            }
        },
    ) { innerPadding ->

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            !state.hasProfile -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            Dimensions.paddingMedium
                        ),
                    ) {
                        Text(
                            text = "No profile set up yet",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Set up your seller profile to start receiving orders",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Button(
                            onClick = { viewModel.showEditDialog() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            ),
                        ) {
                            Text("Set Up Profile")
                        }
                    }
                }
            }

            state.seller != null -> {
                val seller = state.seller!!
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(
                        bottom = Dimensions.paddingLarge
                    ),
                ) {

                    // Header
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
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = seller.businessName
                                            .first()
                                            .uppercaseChar()
                                            .toString(),
                                        style = MaterialTheme.typography.displayMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }

                                Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

                                Text(
                                    text = seller.businessName,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = seller.marketName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )

                                Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(
                                        Dimensions.paddingLarge
                                    ),
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "★ ${seller.rating}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                        Text(
                                            text = "Rating",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = seller.totalRatings.toString(),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                        Text(
                                            text = "Reviews",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = seller.products.size.toString(),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                        Text(
                                            text = "Products",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Business details
                    item {
                        Spacer(modifier = Modifier.height(Dimensions.paddingLarge))
                        Text(
                            text = "Business Details",
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
                                modifier = Modifier.padding(Dimensions.paddingMedium),
                                verticalArrangement = Arrangement.spacedBy(
                                    Dimensions.paddingSmall
                                ),
                            ) {
                                ProfileDetailRow(
                                    label = "Business Name",
                                    value = seller.businessName,
                                )
                                HorizontalDivider()
                                ProfileDetailRow(
                                    label = "Market",
                                    value = seller.marketName,
                                )
                                if (seller.description.isNotBlank()) {
                                    HorizontalDivider()
                                    ProfileDetailRow(
                                        label = "Description",
                                        value = seller.description,
                                    )
                                }
                                if (seller.openingTime != null) {
                                    HorizontalDivider()
                                    ProfileDetailRow(
                                        label = "Opening Time",
                                        value = seller.openingTime,
                                    )
                                }
                                if (seller.closingTime != null) {
                                    HorizontalDivider()
                                    ProfileDetailRow(
                                        label = "Closing Time",
                                        value = seller.closingTime,
                                    )
                                }
                                HorizontalDivider()
                                ProfileDetailRow(
                                    label = "Status",
                                    value = if (seller.isOpen) "Open" else "Closed",
                                )
                            }
                        }
                    }

                    // Theme selector
                    item {
                        Spacer(modifier = Modifier.height(Dimensions.paddingLarge))
                        Text(
                            text = "Appearance",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                horizontal = Dimensions.paddingLarge,
                                vertical = Dimensions.paddingSmall,
                            ),
                        )
                    }

                    item {
                        val themeMode by themeViewModel.themeMode.collectAsState()

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
                                modifier = Modifier.padding(Dimensions.paddingMedium),
                            ) {
                                Text(
                                    text = "Theme",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )

                                Spacer(modifier = Modifier.height(Dimensions.paddingMedium))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(
                                        Dimensions.paddingSmall
                                    ),
                                ) {
                                    ThemeOption(
                                        label = "Light",
                                        icon = Icons.Filled.LightMode,
                                        selected = themeMode == ThemeMode.LIGHT,
                                        onClick = { themeViewModel.setTheme(ThemeMode.LIGHT) },
                                        modifier = Modifier.weight(1f),
                                    )
                                    ThemeOption(
                                        label = "Dark",
                                        icon = Icons.Filled.DarkMode,
                                        selected = themeMode == ThemeMode.DARK,
                                        onClick = { themeViewModel.setTheme(ThemeMode.DARK) },
                                        modifier = Modifier.weight(1f),
                                    )
                                    ThemeOption(
                                        label = "System",
                                        icon = Icons.Filled.SettingsBrightness,
                                        selected = themeMode == ThemeMode.SYSTEM,
                                        onClick = { themeViewModel.setTheme(ThemeMode.SYSTEM) },
                                        modifier = Modifier.weight(1f),
                                    )
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
                            shape = RoundedCornerShape(Dimensions.radiusFull),
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

@Composable
private fun ProfileDetailRow(
    label: String,
    value: String,
) {
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

@Composable
private fun SellerProfileDialog(
    seller: com.bulkbasket.domain.model.Seller?,
    onDismiss: () -> Unit,
    onSave: (
        businessName: String,
        marketName: String,
        description: String,
        openingTime: String,
        closingTime: String,
    ) -> Unit,
) {
    var businessName by remember {
        mutableStateOf(seller?.businessName ?: "")
    }
    var marketName by remember {
        mutableStateOf(seller?.marketName ?: "")
    }
    var description by remember {
        mutableStateOf(seller?.description ?: "")
    }
    var openingTime by remember {
        mutableStateOf(seller?.openingTime ?: "")
    }
    var closingTime by remember {
        mutableStateOf(seller?.closingTime ?: "")
    }
    var businessNameError by remember { mutableStateOf(false) }
    var marketNameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (seller != null) "Edit Profile" else "Set Up Profile",
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    Dimensions.paddingSmall
                ),
            ) {
                OutlinedTextField(
                    value = businessName,
                    onValueChange = {
                        businessName = it
                        businessNameError = false
                    },
                    label = { Text("Business Name") },
                    isError = businessNameError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = marketName,
                    onValueChange = {
                        marketName = it
                        marketNameError = false
                    },
                    label = { Text("Market Name") },
                    isError = marketNameError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                )
                OutlinedTextField(
                    value = openingTime,
                    onValueChange = { openingTime = it },
                    label = { Text("Opening Time (e.g. 07:00:00)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = closingTime,
                    onValueChange = { closingTime = it },
                    label = { Text("Closing Time (e.g. 18:00:00)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    businessNameError = businessName.isBlank()
                    marketNameError = marketName.isBlank()
                    if (!businessNameError && !marketNameError) {
                        onSave(
                            businessName,
                            marketName,
                            description,
                            openingTime,
                            closingTime,
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Text(if (seller != null) "Update" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun ThemeOption(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.radiusSmall),
        color = if (selected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        border = if (selected) androidx.compose.foundation.BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary,
        ) else null,
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.paddingSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            )
        }
    }
}
