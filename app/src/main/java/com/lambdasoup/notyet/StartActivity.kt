package com.lambdasoup.notyet

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.button.MaterialButton
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KMutableProperty0


private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")

class StartActivity : AppCompatActivity() {
    private lateinit var targetTime: LocalTime
    private var preTargetMinutes: Int = 0
    @ColorInt private var initialBgColor: Int = 0
    @ColorInt private var preTargetBgColor: Int = 0
    @ColorInt private var targetBgColor: Int = 0
    @ColorInt private var dialColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_start)

        initModel()

        findViewById<MaterialButton>(R.id.target_time).setOnClickListener {
            it.requestFocus()

            TimePickerFragment()
                .withInitialValue(targetTime)
                .withCallback { selectedTime ->
                    targetTime = selectedTime
                    rerender()
                }
                .show(supportFragmentManager, "target_time_timepicker_fragment")
        }

        findViewById<EditText>(R.id.pre_target_minutes).apply {
            if (DigitsFilter !in filters) {
                filters = filters.plusElement(DigitsFilter)
            }

            addTextChangedListener {
                preTargetMinutes = try {
                    it?.toString()?.toInt()
                } catch (e: NumberFormatException) {
                    0
                } ?: 0
            }
        }

        findViewById<MaterialButton>(R.id.initial_bg_color).setOnClickListener {
            showColorPickerFor(this::initialBgColor)
        }

        findViewById<MaterialButton>(R.id.pre_target_bg_color).setOnClickListener {
            showColorPickerFor(this::preTargetBgColor)
        }

        findViewById<MaterialButton>(R.id.target_bg_color).setOnClickListener {
            showColorPickerFor(this::targetBgColor)
        }

        findViewById<MaterialButton>(R.id.dial_color).setOnClickListener {
            showColorPickerFor(this::dialColor)
        }

        findViewById<MaterialButton>(R.id.start_button).setOnClickListener {
            it.requestFocus()
            startClock()
        }

        rerender()
    }

    private fun showColorPickerFor(colorProperty: KMutableProperty0<Int>) {
        ColorPickerDialog.Builder(this)
            .setPositiveButton(getString(android.R.string.ok),
                ColorEnvelopeListener { envelope, _ ->
                    colorProperty.set(envelope.color)
                    rerender()
                })
            .setNegativeButton(getString(android.R.string.cancel)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .attachAlphaSlideBar(false)
            .attachBrightnessSlideBar(true)
            .setBottomSpace(12)
            .also {
                it.colorPickerView.apply {
                    setInitialColor(colorProperty.get())
                }
            }
            .show()
    }

    private fun initModel() {
        targetTime = LocalTime.now()
        preTargetMinutes = 1

        initialBgColor = Color.RED
        preTargetBgColor = Color.YELLOW
        targetBgColor = Color.GREEN
        dialColor = Color.LTGRAY
    }

    private fun rerender() {
        findViewById<MaterialButton>(R.id.target_time).text = targetTime.format(TIME_FORMATTER)

        findViewById<EditText>(R.id.pre_target_minutes).setText(preTargetMinutes.toString())

        findViewById<MaterialButton>(R.id.initial_bg_color).backgroundTintList = ColorStateList.valueOf(initialBgColor)

        findViewById<MaterialButton>(R.id.pre_target_bg_color).backgroundTintList = ColorStateList.valueOf(preTargetBgColor)

        findViewById<MaterialButton>(R.id.target_bg_color).backgroundTintList = ColorStateList.valueOf(targetBgColor)

        findViewById<MaterialButton>(R.id.dial_color).backgroundTintList = ColorStateList.valueOf(dialColor)
    }

    private fun startClock() {
        val now = LocalDateTime.now().withSecond(0).withNano(0)
        val targetDateTime = run {
            val targetToday = now.withHour(targetTime.hour).withMinute(targetTime.minute)
            if (targetToday > now) {
                targetToday
            } else {
                now.plusDays(1L).withHour(targetTime.hour).withMinute(targetTime.minute)
            }
        }

        val closeToTargetTime = targetDateTime.minusMinutes(preTargetMinutes.toLong())
        Log.d("START", "closeToTargetTime=$closeToTargetTime")

        startActivity(
            ClockActivity.getIntent(
                applicationContext,
                closeToTargetTime = closeToTargetTime,
                targetTime = targetDateTime,
                dialColor = dialColor,
                farFromTargetBgColor = initialBgColor,
                closeToTargetBgColor = preTargetBgColor,
                targetBgColor = targetBgColor,
            )
        )
    }
}
