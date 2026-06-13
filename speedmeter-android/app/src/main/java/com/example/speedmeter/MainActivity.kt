package com.example.speedmeter

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.speedmeter.service.SpeedMonitorService
import com.example.speedmeter.ui.SpeedScreen
import com.example.speedmeter.ui.theme.SpeedMeterTheme

/**
 * Single-screen entry point. Hosts the Compose UI and owns the
 * start/stop intents and the runtime notification-permission flow.
 */
class MainActivity : ComponentActivity() {

    // If the user grants notifications, start immediately; otherwise we still
    // start (the meter works without the notification, just without the icon).
    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            startMonitoring()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpeedMeterTheme {
                SpeedScreen(
                    onStart = ::ensurePermissionThenStart,
                    onStop = ::stopMonitoring,
                )
            }
        }
    }

    private fun ensurePermissionThenStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }
        startMonitoring()
    }

    private fun startMonitoring() {
        ContextCompat.startForegroundService(this, Intent(this, SpeedMonitorService::class.java))
    }

    private fun stopMonitoring() {
        startService(
            Intent(this, SpeedMonitorService::class.java).apply {
                action = SpeedMonitorService.ACTION_STOP
            },
        )
    }

    companion object {
        /** Tapping the notification re-opens the app. */
        fun pendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            return PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        /** The notification's "Stop" action target. */
        fun stopServicePendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, SpeedMonitorService::class.java).apply {
                action = SpeedMonitorService.ACTION_STOP
            }
            return PendingIntent.getService(
                context, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }
    }
}
