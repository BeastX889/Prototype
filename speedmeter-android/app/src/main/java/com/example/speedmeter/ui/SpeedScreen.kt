package com.example.speedmeter.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.speedmeter.R
import com.example.speedmeter.model.SpeedSample
import com.example.speedmeter.service.SpeedState
import com.example.speedmeter.util.SpeedFormatter

@Composable
fun SpeedScreen(
    onStart: () -> Unit,
    onStop: () -> Unit,
) {
    val sample: SpeedSample by SpeedState.sample.collectAsStateWithLifecycle()
    val running: Boolean by SpeedState.running.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResourceTitle(),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(40.dp))

            DirectionRow(label = "↓ Download", value = SpeedFormatter.format(sample.rxBytesPerSec))
            Spacer(Modifier.height(16.dp))
            DirectionRow(label = "↑ Upload", value = SpeedFormatter.format(sample.txBytesPerSec))

            Spacer(Modifier.height(48.dp))

            Button(
                onClick = { if (running) onStop() else onStart() },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (running) "Stop" else "Start")
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = if (running) "Monitoring active — check your status bar"
                else "Tap Start to show speed in the status bar",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun DirectionRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
        )
    }
}

@Composable
private fun stringResourceTitle(): String =
    androidx.compose.ui.res.stringResource(R.string.app_name)
