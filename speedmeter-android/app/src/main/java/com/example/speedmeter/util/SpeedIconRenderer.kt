package com.example.speedmeter.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import com.example.speedmeter.model.SpeedSample

/**
 * Renders the current speed as a small two-line bitmap so it can be used as the
 * notification's status-bar icon — the trick that puts the live numbers up in
 * the status bar, mimicking the old Samsung indicator.
 *
 * The icon is split into a top half (download) and bottom half (upload). Each
 * half shows the numeric value in bold with its unit in a smaller font directly
 * beneath. White is used so the system tints the icon correctly in the status bar.
 */
object SpeedIconRenderer {

    private const val SIZE = 96 // px; the system scales this down to status-bar size
    private const val MID = SIZE / 2f

    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = 40f
    }

    private val unitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT
        textSize = 26f
    }

    /** Builds a fresh ARGB_8888 bitmap showing both directions of [sample]. */
    fun render(sample: SpeedSample): Bitmap {
        val bitmap = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val (downValue, downUnit) = split(SpeedFormatter.format(sample.rxBytesPerSec))
        val (upValue, upUnit) = split(SpeedFormatter.format(sample.txBytesPerSec))

        // Top half: download (value baseline ~34, unit baseline ~46).
        canvas.drawText(downValue, MID, 34f, valuePaint)
        canvas.drawText(downUnit, MID, 46f, unitPaint)
        // Bottom half: upload (value baseline ~80, unit baseline ~92).
        canvas.drawText(upValue, MID, 80f, valuePaint)
        canvas.drawText(upUnit, MID, 92f, unitPaint)

        return bitmap
    }

    /** Splits "1.2 MB/s" -> ("1.2", "MB/s"); the "/s" is implied by context. */
    private fun split(formatted: String): Pair<String, String> {
        val idx = formatted.indexOf(' ')
        if (idx == -1) return formatted to ""
        val value = formatted.substring(0, idx)
        // Drop the "/s" suffix to keep the tiny icon legible (e.g. "MB").
        val unit = formatted.substring(idx + 1).removeSuffix("/s")
        return value to unit
    }
}
