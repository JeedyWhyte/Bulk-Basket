package com.bulkbasket.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.bulkbasket.ui.common.navigation.BulkBasketNavHost
import com.bulkbasket.ui.theme.BulkBasketTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BulkBasketTheme {
                val navController = rememberNavController()
                BulkBasketNavHost(navController = navController)
            }
        }
    }
}