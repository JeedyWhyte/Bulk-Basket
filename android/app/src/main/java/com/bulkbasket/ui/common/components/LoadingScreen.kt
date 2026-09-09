package com.bulkbasket.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bulkbasket.ui.theme.Dimensions
import kotlinx.coroutines.delay
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.alpha

// Shimmer placeholder box
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
) {
    var alpha by remember { mutableFloatStateOf(0.3f) }
    val animatedAlpha by animateFloatAsState(
        targetValue = alpha,
        animationSpec = tween(durationMillis = 800),
        label = "shimmer",
        finishedListener = {
            alpha = if (alpha == 0.3f) 0.7f else 0.3f
        }
    )

    LaunchedEffect(Unit) {
        while (true) {
            alpha = if (alpha == 0.3f) 0.7f else 0.3f
            delay(800)
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Dimensions.radiusSmall))
            .background(
                MaterialTheme.colorScheme.onSurface.copy(alpha = animatedAlpha * 0.15f)
            )
    )
}

// Full home screen skeleton
//
// This is rendered as a single item() inside HomeScreen's own LazyColumn, so
// it must NOT be a LazyColumn itself — a lazy list measured inside another
// lazy list's item gets an infinite height constraint and crashes at launch
// (java.lang.IllegalStateException: Vertically scrollable component was
// measured with an infinity maximum height constraints).
@Composable
fun HomeLoadingScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
    ) {
        // Top bar shimmer
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        )

        // Products shimmer
        Spacer(modifier = Modifier.height(Dimensions.paddingLarge))
        ShimmerBox(
            modifier = Modifier
                .padding(horizontal = Dimensions.paddingLarge)
                .width(160.dp)
                .height(24.dp)
        )
        Spacer(modifier = Modifier.height(Dimensions.paddingMedium))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.paddingLarge),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            repeat(2) {
                Column(modifier = Modifier.weight(1f)) {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(16.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(12.dp)
                    )
                }
            }
        }

        // Second row of products
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.paddingLarge),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            repeat(2) {
                Column(modifier = Modifier.weight(1f)) {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(16.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(12.dp)
                    )
                }
            }
        }

        // Sellers shimmer
        Spacer(modifier = Modifier.height(Dimensions.paddingLarge))
        ShimmerBox(
            modifier = Modifier
                .padding(horizontal = Dimensions.paddingLarge)
                .width(140.dp)
                .height(24.dp)
        )
        Spacer(modifier = Modifier.height(Dimensions.paddingMedium))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimensions.paddingLarge),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            repeat(2) {
                ShimmerBox(
                    modifier = Modifier
                        .width(180.dp)
                        .height(100.dp)
                )
            }
        }
    }
}

// Generic full screen loader
@Composable
fun FullScreenLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimensions.paddingLarge),
        verticalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium),
    ) {
        repeat(5) {
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            )
        }
    }
}
