package com.example.speedmeter.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import com.example.speedmeter.model.SpeedSample

/**
 * Renders the current speed as a small bitmap used as the notification's
 * status-bar icon — the trick that puts the live numbers up in the status bar,
 * mimicking the old Samsung indicator.
 *
 * Layout, tuned for legibility at status-bar size:
 *   ┌──────────┐
 *   │   1.2M   │  ← download (top)
 *   │  ──────  │  ← divider
 *   │   240K   │  ← upload (bottom)
 *   └──────────┘
 *
 * Each value is drawn as large as will fit, auto-shrinking on wide strings so it
 * never clips. White on transparent so the system tints it correctly.
 */
object SpeedIconRenderer {

    private const val SIZE = 144           // px; the system scales this down
    private const val MAX_TEXT_WIDTH = 132f // leave a little horizontal padding
    private const val BASE_TEXT_SIZE = 64f

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textSize = BASE_TEXT_SIZE
    }

    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = 110
        strokeWidth = 3f
    }

    /** Builds a fresh ARGB_8888 bitmap showing both directions of [sample]. */
    fun render(sample: SpeedSample): Bitmap {
        val bitmap = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val down = SpeedFormatter.formatCompact(sample.rxBytesPerSec)
        val up = SpeedFormatter.formatCompact(sample.txBytesPerSec)

        // Top: download. Baseline placed so the row sits in the upper half.
        drawFitted(canvas, down, baseline = 58f)
        // Divider between the two readings.
        canvas.drawLine(22f, 74f, SIZE - 22f, 74f, dividerPaint)
        // Bottom: upload.
        drawFitted(canvas, up, baseline = 132f)

        return bitmap
    }

    /** Draws [text] centered, shrinking the font if it would exceed the icon width. */
    private fun drawFitted(canvas: Canvas, text: String, baseline: Float) {
        val width = textPaint.measureText(text)
        val original = textPaint.textSize
        if (width > MAX_TEXT_WIDTH) {
            textPaint.textSize = original * (MAX_TEXT_WIDTH / width)
        }
        canvas.drawText(text, SIZE / 2f, baseline, textPaint)
        textPaint.textSize = original // restore for the next row / next render
    }
}
