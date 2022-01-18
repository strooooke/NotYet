package com.lambdasoup.notyet

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.cos
import kotlin.math.sin

class ClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var cx: Float = 0f
    private var cy: Float = 0f
    private var dialOuterRadius: Float = 0f
    private var dialInnerRadius: Float = 0f
    private var tickInnerRadius: Float = 0f
    private var textCenterRadius: Float = 0f
    private var hourHandRadius: Float = 0f
    private var minuteHandRadius: Float = 0f

    private var currTime = LocalDateTime.now()

    var targetTime = LocalDateTime.MAX
    set(value) {
        field = value
        onTimeChanged()
    }

    var closeToTargetTime = LocalDateTime.MAX
    set(value) {
        field = value
        onTimeChanged()
    }

    @ColorInt var farFromTargetBackgroundColor = Color.RED
    set(value) {
        field = value
        invalidate()
    }

    @ColorInt var closeToTargetBackgroundColor = Color.YELLOW
    set(value) {
        field = value
        invalidate()
    }

    @ColorInt var targetBackgroundColor = Color.GREEN
    set(value) {
        field = value
        invalidate()
    }

    @ColorInt var dialColor = Color.WHITE
    set(value) {
        field = value
        textPaint.color = value
        dialPaint.color = value
        tickPaint.color = value
        invalidate()
    }

    private var textPaint = TextPaint().apply {
        isAntiAlias = true
        color = dialColor
        textAlign = Paint.Align.CENTER
    }
    private var dialPaint = Paint().apply {
        isAntiAlias = true
        color = dialColor
    }
    private var tickPaint = Paint().apply {
        isAntiAlias = true
        color = dialColor
        style = Paint.Style.STROKE
    }
    private var backgroundPaint = Paint().apply {
        color = Color.RED
    }
    private var hourHandPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private var minuteHandPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val timeChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onTimeChanged()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        context.registerReceiver(
            timeChangeReceiver,
            IntentFilter().apply {
                addAction(Intent.ACTION_TIME_TICK)
                addAction(Intent.ACTION_TIME_CHANGED)
                addAction(Intent.ACTION_TIMEZONE_CHANGED)
            }
        )

        onTimeChanged()
    }

    override fun onDetachedFromWindow() {
        context.unregisterReceiver(timeChangeReceiver)
        super.onDetachedFromWindow()
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
        minuteHandRadius = size * 0.26f
        hourHandRadius = size * 0.2f
        minuteHandPaint.apply {
            strokeWidth = size * 0.01f
        }
        hourHandPaint.apply {
            strokeWidth = size * 0.02f
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (closeToTargetTime > targetTime) {
            throw IllegalStateException("Target time $targetTime should not be before closeToTargetTime $closeToTargetTime")
        }

        backgroundPaint.color = when {
            currTime < closeToTargetTime -> farFromTargetBackgroundColor
            currTime < targetTime -> closeToTargetBackgroundColor
            else -> targetBackgroundColor
        }

        drawDial(canvas)

        val ghostHandsColor = mixRgb(backgroundPaint.color, dialColor)
        drawHands(canvas, targetTime.toLocalTime(), ghostHandsColor)
        drawHands(canvas, currTime.toLocalTime(), dialColor)
    }

    private fun drawHands(canvas: Canvas, time: LocalTime, @ColorInt color: Int) {
        val currMinute = time.minute
        minuteHandPaint.color = color
        canvas.drawLine(
            cx,
            cy,
            cx + minuteHandRadius * cos(Math.PI / 30 * (currMinute - 15)).toFloat(),
            cy + minuteHandRadius * sin(Math.PI / 30 * (currMinute - 15)).toFloat(),
            minuteHandPaint
        )

        val currHour = (time.hour % 12) + currMinute / 60f
        hourHandPaint.color = color
        canvas.drawLine(
            cx,
            cy,
            cx + hourHandRadius * cos(Math.PI / 6 * (currHour - 3)).toFloat(),
            cy + hourHandRadius * sin(Math.PI / 6 * (currHour - 3)).toFloat(),
            hourHandPaint
        )
    }

    private fun drawDial(canvas: Canvas) {
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

    private fun onTimeChanged() {
        currTime = LocalDateTime.now()
        invalidate()
    }
}

@ColorInt private fun mixRgb(@ColorInt c1: Int, @ColorInt c2: Int): Int {
    val r = (c1.red + c2.red) / 2
    val g = (c1.green + c2.green) / 2
    val b = (c1.blue + c2.blue) / 2
    val a = 0xff
    return (a shl 24) + (r shl 16) + (g shl 8) + b
}
