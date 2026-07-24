package com.bulkbasket.ui.rider.jobs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bulkbasket.ui.rider.common.DeliveryCard
import com.bulkbasket.ui.theme.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiderJobsScreen(
    modifier: Modifier = Modifier,
    viewModel: RiderJobsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Available", "Active")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Deliveries",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Welcome, ${state.username}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(
                                alpha = 0.8f
                            ),
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

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index)
                                    FontWeight.Bold
                                else
                                    FontWeight.Normal,
                            )
                        },
                    )
                }
            }

            PullToRefreshBox(
                isRefreshing = state.isLoading,
                onRefresh = { viewModel.loadJobs() },
                modifier = Modifier.fillMaxSize(),
            ) {
                when (selectedTab) {
                    0 -> {
                        // Available deliveries
                        if (state.availableDeliveries.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "No available deliveries",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    Dimensions.paddingLarge
                                ),
                                verticalArrangement = Arrangement.spacedBy(
                                    Dimensions.paddingMedium
                                ),
                            ) {
                                items(state.availableDeliveries) { delivery ->
                                    DeliveryCard(
                                        delivery = delivery,
                                        onAccept = { id ->
                                            viewModel.acceptDelivery(id)
                                        },
                                    )
                                }
                            }
                        }
                    }
                    1 -> {
                        // Active deliveries
                        if (state.activeDeliveries.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "No active deliveries",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    Dimensions.paddingLarge
                                ),
                                verticalArrangement = Arrangement.spacedBy(
                                    Dimensions.paddingMedium
                                ),
                            ) {
                                items(state.activeDeliveries) { delivery ->
                                    DeliveryCard(
                                        delivery = delivery,
                                        onUpdateStatus = { id, status ->
                                            viewModel.updateDeliveryStatus(
                                                id, status
                                            )
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}