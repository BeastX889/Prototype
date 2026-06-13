package com.example.speedmeter.service

import com.example.speedmeter.model.SpeedSample
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Process-wide holder that bridges the monitoring service and the UI without the
 * ceremony of a bound service. The service writes the latest [SpeedSample] and
 * whether monitoring is active; the Activity collects these as Compose state.
 */
object SpeedState {

    private val _sample = MutableStateFlow(SpeedSample.ZERO)
    val sample: StateFlow<SpeedSample> = _sample.asStateFlow()

    private val _running = MutableStateFlow(false)
    val running: StateFlow<Boolean> = _running.asStateFlow()

    internal fun update(sample: SpeedSample) {
        _sample.value = sample
    }

    internal fun setRunning(running: Boolean) {
        _running.value = running
        if (!running) _sample.value = SpeedSample.ZERO
    }
}
