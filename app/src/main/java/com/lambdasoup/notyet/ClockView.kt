package com.lambdasoup.notyet

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class ClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var dialOuterRadius: Float = 0f
    private var cx: Float = 0f
    private var cy: Float = 0f
    private var dialInnerRadius: Float = 0f
    private var tickInnerRadius: Float = 0f
    private var textCenterRadius: Float = 0f

    private var textPaint = TextPaint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
    }
    private var dialPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
    }
    private var tickPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        style = Paint.Style.STROKE
    }
    private var backgroundPaint = Paint().apply {
        color = Color.RED
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val width = right - left
        val height = bottom - top
        val size = width.coerceAtMost(height)

        dialOuterRadius = size * 0.33f
        cx = width / 2f
        cy = height / 2f
        dialInnerRadius = size * 0.32f
        tickInnerRadius = size * 0.28f
        textCenterRadius = size * 0.42f
        textPaint.apply {
            textSize = size * 0.1f
            baselineShift = (textSize / 2 - descent()).toInt()
        }
        tickPaint.strokeWidth = size * 0.01f
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(cx, cy, dialOuterRadius, dialPaint)
        canvas.drawCircle(cx, cy, dialInnerRadius, backgroundPaint)

        for (h in 1..12) {
            val bx = cos(Math.PI / 6 * (h - 3)).toFloat()
            val by = sin(Math.PI / 6 * (h - 3)).toFloat()
            canvas.drawLine(
                cx + tickInnerRadius * bx,
                cy + tickInnerRadius * by,
                cx + dialOuterRadius * bx,
                cy + dialOuterRadius * by,
                tickPaint
            )

            val textCx = cx + textCenterRadius * bx
            val textCy = cy + textCenterRadius * by

            canvas.drawText(
                h.toString(),
                textCx,
                textCy + textPaint.baselineShift,
                textPaint
            )
        }
    }
}
