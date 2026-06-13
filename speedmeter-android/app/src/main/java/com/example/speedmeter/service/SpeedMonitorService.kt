package com.example.speedmeter.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.TrafficStats
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.speedmeter.MainActivity
import com.example.speedmeter.R
import com.example.speedmeter.model.SpeedSample
import com.example.speedmeter.util.SpeedFormatter
import com.example.speedmeter.util.SpeedIconRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Foreground service that samples total device traffic once per second using
 * [TrafficStats], publishes the result to [SpeedState], and keeps a live,
 * silent notification whose small icon shows the current speed in the status bar.
 */
class SpeedMonitorService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }

        // Promote to foreground immediately with a zero reading to satisfy the
        // 5-second startForeground requirement, then begin sampling.
        startForeground(NOTIFICATION_ID, buildNotification(SpeedSample.ZERO))
        SpeedState.setRunning(true)
        startSampling()
        return START_STICKY
    }

    private fun startSampling() = scope.launch {
        var lastRx = TrafficStats.getTotalRxBytes()
        var lastTx = TrafficStats.getTotalTxBytes()
        var lastTime = SystemClock.elapsedRealtime()

        while (isActive) {
            delay(SAMPLE_INTERVAL_MS)

            val rx = TrafficStats.getTotalRxBytes()
            val tx = TrafficStats.getTotalTxBytes()
            val now = SystemClock.elapsedRealtime()
            val elapsedSec = (now - lastTime) / 1000.0

            // TrafficStats returns UNSUPPORTED (-1) on some devices/emulators.
            val sample = if (rx < 0 || tx < 0 || elapsedSec <= 0) {
                SpeedSample.ZERO
            } else {
                SpeedSample(
                    rxBytesPerSec = ((rx - lastRx) / elapsedSec).toLong().coerceAtLeast(0),
                    txBytesPerSec = ((tx - lastTx) / elapsedSec).toLong().coerceAtLeast(0),
                )
            }

            lastRx = rx
            lastTx = tx
            lastTime = now

            SpeedState.update(sample)
            notificationManager.notify(NOTIFICATION_ID, buildNotification(sample))
        }
    }

    private fun buildNotification(sample: SpeedSample): Notification {
        val contentIntent = MainActivity.pendingIntent(this)
        val stopIntent = MainActivity.stopServicePendingIntent(this)

        val text = "↓ ${SpeedFormatter.format(sample.rxBytesPerSec)}   " +
            "↑ ${SpeedFormatter.format(sample.txBytesPerSec)}"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(IconCompat.createWithBitmap(SpeedIconRenderer.render(sample)))
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(text)
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .addAction(0, getString(R.string.action_stop), stopIntent)
            .build()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_LOW, // silent, no sound/vibration
            ).apply {
                description = getString(R.string.channel_description)
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        SpeedState.setRunning(false)
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_STOP = "com.example.speedmeter.action.STOP"
        private const val CHANNEL_ID = "speed_monitor"
        private const val NOTIFICATION_ID = 1
        private const val SAMPLE_INTERVAL_MS = 1000L
    }
}
