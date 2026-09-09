package com.bulkbasket.ui.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bulkbasket.ui.theme.Dimensions

@Composable
fun CurvedTopAppBar(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(
            bottomStart = Dimensions.topBarCurve,
            bottomEnd = Dimensions.topBarCurve,
        ),
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(
                    start = Dimensions.paddingLarge,
                    end = Dimensions.paddingLarge,
                    top = Dimensions.paddingMedium,
                    bottom = Dimensions.paddingXLarge,
                ),
            content = content,
        )
    }
}
