package com.example.speedmeter.model

/**
 * A single throughput reading, expressed in bytes-per-second for each direction.
 *
 * @param rxBytesPerSec download rate (bytes received per second)
 * @param txBytesPerSec upload rate (bytes transmitted per second)
 */
data class SpeedSample(
    val rxBytesPerSec: Long,
    val txBytesPerSec: Long,
) {
    companion object {
        val ZERO = SpeedSample(0L, 0L)
    }
}
