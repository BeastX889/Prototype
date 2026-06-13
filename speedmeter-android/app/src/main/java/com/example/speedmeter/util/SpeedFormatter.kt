package com.example.speedmeter.util

import java.util.Locale

/**
 * Formats a byte-per-second throughput value into a compact, human-readable
 * string with an auto-scaled unit (B/s, KB/s, MB/s, GB/s).
 *
 * Uses decimal (1000-based) scaling, matching how mobile carriers and the
 * original Samsung indicator present speeds. Pure logic — no Android types — so
 * it is fully unit-testable on the JVM.
 */
object SpeedFormatter {

    private const val KB = 1000.0
    private const val MB = KB * 1000.0
    private const val GB = MB * 1000.0

    /** Returns the numeric + unit string, e.g. "1.2 MB/s", "968 KB/s", "0 B/s". */
    fun format(bytesPerSec: Long): String {
        val value = if (bytesPerSec < 0) 0L else bytesPerSec
        return when {
            value < KB -> "$value B/s"
            value < MB -> "${trim(value / KB)} KB/s"
            value < GB -> "${trim(value / MB)} MB/s"
            else -> "${trim(value / GB)} GB/s"
        }
    }

    /**
     * One decimal place, but drops a trailing ".0" so whole numbers read cleanly
     * ("12 MB/s" rather than "12.0 MB/s").
     */
    private fun trim(scaled: Double): String {
        val rounded = String.format(Locale.US, "%.1f", scaled)
        return if (rounded.endsWith(".0")) rounded.dropLast(2) else rounded
    }
}
