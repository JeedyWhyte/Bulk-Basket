package com.bulkbasket.ui.buyer.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bulkbasket.ui.common.components.DetailScaffold
import com.bulkbasket.ui.theme.Dimensions

@Composable
fun CloseAccountScreen(
    onBack: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onAccountClosed: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: CloseAccountViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.closed) {
        if (state.closed) onAccountClosed()
    }

    DetailScaffold(
        title = "Close Account",
        onBack = onBack,
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(Dimensions.paddingLarge),
            verticalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Spacer(modifier = Modifier.height(Dimensions.paddingLarge))

            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(56.dp),
            )

            Text(
                text = "Deactivate Your Account",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimensions.radiusMedium))
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(Dimensions.paddingMedium),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "This immediately signs you out and disables sign-in.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
                Text(
                    text = "Your order and review history is kept (other users may " +
                        "still reference it) but hidden from your account. To " +
                        "permanently erase your data instead, contact support below.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    lineHeight = 20.sp,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Dimensions.radiusMedium))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(Dimensions.paddingMedium),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "What happens when you deactivate",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                listOf(
                    "You are signed out on every device",
                    "You can no longer log back in",
                    "Your profile is hidden from sellers and riders",
                    "Reactivation requires contacting support",
                ).forEach { item ->
                    Text(
                        text = "• $item",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.height(Dimensions.paddingSmall))

            Button(
                onClick = { viewModel.showConfirmDialog() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimensions.buttonHeight),
                shape = RoundedCornerShape(Dimensions.radiusFull),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Text(
                    text = "Deactivate My Account",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            OutlinedButton(
                onClick = onNavigateToHelp,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimensions.radiusFull),
            ) {
                Text("Contact Support to Permanently Delete Data")
            }
        }
    }

    if (state.showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideConfirmDialog() },
            title = { Text("Deactivate account?", fontWeight = FontWeight.Bold) },
            text = { Text("You will be signed out immediately and won't be able to log back in. This cannot be undone from the app.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.closeAccount() },
                    enabled = !state.isClosing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    if (state.isClosing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onError,
                        )
                    } else {
                        Text("Deactivate")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideConfirmDialog() }) {
                    Text("Cancel")
                }
            },
        )
    }
}
