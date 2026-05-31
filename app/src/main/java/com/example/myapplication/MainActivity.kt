package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.ui.CryptoViewModel
import com.example.myapplication.ui.screen.MainScreen
import com.example.myapplication.ui.screen.SettingsScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.worker.UpdateWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val viewModel: CryptoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Schedule periodic update worker (runs every 15 minutes)
        schedulePeriodicUpdate()
        
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf("main") }
                    
                    when (currentScreen) {
                        "main" -> {
                            MainScreen(
                                viewModel = viewModel,
                                onNavigateToSettings = { currentScreen = "settings" }
                            )
                        }
                        "settings" -> {
                            SettingsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { currentScreen = "main" }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun schedulePeriodicUpdate() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<UpdateWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "BitcoinFearAndTiltPeriodicUpdate",
            ExistingPeriodicWorkPolicy.KEEP, // Keep existing work to avoid resetting periodic timers
            periodicWorkRequest
        )
    }
}