package com.bulkbasket.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.compose.rememberNavController
import com.bulkbasket.ui.common.navigation.BulkBasketNavHost
import com.bulkbasket.ui.theme.BulkBasketTheme
import com.bulkbasket.ui.theme.Primary700
import com.bulkbasket.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                Primary700.toArgb()
            )
        )
        setContent {
            val themeMode by themeViewModel.themeMode.collectAsState()

            BulkBasketTheme(themeMode = themeMode) {
                val navController = rememberNavController()
                BulkBasketNavHost(navController = navController)
            }
        }
    }
}
