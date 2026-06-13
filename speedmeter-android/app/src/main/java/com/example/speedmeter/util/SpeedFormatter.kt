package com.example.speedmeter.util

import java.util.Locale
import kotlin.math.roundToInt

/**
 * Formats a byte-per-second throughput value into human-readable text.
 *
 * Two flavours:
 *  - [format] — full label like "1.2 MB/s" for the in-app readout and the
 *    notification's expanded content.
 *  - [formatCompact] — short label like "1.2M" / "240K" for the tiny status-bar
 *    icon, where every pixel counts and legibility matters most.
 *
 * Uses decimal (1000-based) scaling, matching how mobile carriers and the
 * original Samsung indicator present speeds. Pure logic — no Android types — so
 * it is fully unit-testable on the JVM.
 */
object SpeedFormatter {

    private const val KB = 1000.0
    private const val MB = KB * 1000.0
    private const val GB = MB * 1000.0

    /** Full label, e.g. "1.2 MB/s", "968 KB/s", "0 B/s". */
    fun format(bytesPerSec: Long): String {
        val value = bytesPerSec.coerceAtLeast(0)
        return when {
            value < KB -> "$value B/s"
            value < MB -> "${oneDecimal(value / KB)} KB/s"
            value < GB -> "${oneDecimal(value / MB)} MB/s"
            else -> "${oneDecimal(value / GB)} GB/s"
        }
    }

    /**
     * Compact label for the status-bar icon, e.g. "1.2M", "240K", "12M", "999", "0".
     * A single-letter unit (K/M/G, none for bytes) keeps it short, and the number
     * is held to ~3 significant digits so it never crowds the icon.
     */
    fun formatCompact(bytesPerSec: Long): String {
        val value = bytesPerSec.coerceAtLeast(0)
        return when {
            value < KB -> value.toString()
            value < MB -> compact(value / KB) + "K"
            value < GB -> compact(value / MB) + "M"
            else -> compact(value / GB) + "G"
        }
    }

    /** One decimal below 10 (e.g. "1.2"), whole number above (e.g. "12"). */
    private fun compact(scaled: Double): String =
        if (scaled < 10) oneDecimal(scaled) else scaled.roundToInt().toString()

    /** One decimal place, dropping a trailing ".0" so whole numbers read cleanly. */
    private fun oneDecimal(scaled: Double): String {
        val rounded = String.format(Locale.US, "%.1f", scaled)
        return if (rounded.endsWith(".0")) rounded.dropLast(2) else rounded
    }
}
