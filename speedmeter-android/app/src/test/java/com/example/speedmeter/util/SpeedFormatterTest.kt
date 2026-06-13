package com.example.speedmeter.util

import org.junit.Assert.assertEquals
import org.junit.Test

class SpeedFormatterTest {

    @Test
    fun zero_isBytes() {
        assertEquals("0 B/s", SpeedFormatter.format(0))
    }

    @Test
    fun negative_clampsToZero() {
        assertEquals("0 B/s", SpeedFormatter.format(-500))
    }

    @Test
    fun belowKilo_staysInBytes() {
        assertEquals("999 B/s", SpeedFormatter.format(999))
    }

    @Test
    fun kiloBoundary_rollsToKb() {
        assertEquals("1 KB/s", SpeedFormatter.format(1000))
    }

    @Test
    fun kilobytes_haveOneDecimalWhenNeeded() {
        assertEquals("1.2 KB/s", SpeedFormatter.format(1240))
    }

    @Test
    fun megaBoundary_rollsToMb() {
        assertEquals("1 MB/s", SpeedFormatter.format(1_000_000))
    }

    @Test
    fun megabytes_roundToOneDecimal() {
        assertEquals("1.2 MB/s", SpeedFormatter.format(1_200_000))
    }

    @Test
    fun gigaBoundary_rollsToGb() {
        assertEquals("1 GB/s", SpeedFormatter.format(1_000_000_000))
    }

    @Test
    fun wholeNumbers_dropTrailingZero() {
        assertEquals("12 MB/s", SpeedFormatter.format(12_000_000))
    }

    // --- compact (status-bar icon) variant ---

    @Test
    fun compact_zero() {
        assertEquals("0", SpeedFormatter.formatCompact(0))
    }

    @Test
    fun compact_bytes_haveNoSuffix() {
        assertEquals("500", SpeedFormatter.formatCompact(500))
    }

    @Test
    fun compact_kilo_withDecimalBelowTen() {
        assertEquals("1.2K", SpeedFormatter.formatCompact(1240))
    }

    @Test
    fun compact_kilo_wholeAboveTen() {
        assertEquals("240K", SpeedFormatter.formatCompact(240_000))
    }

    @Test
    fun compact_mega_withDecimal() {
        assertEquals("1.2M", SpeedFormatter.formatCompact(1_200_000))
    }

    @Test
    fun compact_mega_wholeAboveTen() {
        assertEquals("12M", SpeedFormatter.formatCompact(12_000_000))
    }

    @Test
    fun compact_giga() {
        assertEquals("1G", SpeedFormatter.formatCompact(1_000_000_000))
    }

    @Test
    fun compact_negativeClampsToZero() {
        assertEquals("0", SpeedFormatter.formatCompact(-10))
    }
}
