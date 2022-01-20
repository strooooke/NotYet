package com.lambdasoup.notyet

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.widget.addTextChangedListener
import com.google.android.material.button.MaterialButton
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KMutableProperty0


private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")

private const val PREF_KEY_TARGET_TIME = "target_time"
private const val PREF_KEY_PRE_TARGET_MINUTES = "pre_target_minutes"
private const val PREF_KEY_INITIAL_BG_COLOR = "initial_bg_color"
private const val PREF_KEY_PRE_TARGET_BG_COLOR = "pre_target_bg_color"
private const val PREF_KEY_TARGET_BG_COLOR = "target_bg_color"
private const val PREF_KEY_DIAL_COLOR = "dial_color"

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

        if (savedInstanceState == null) {
            initModel()
        } else {
            restoreInstanceState(savedInstanceState)
        }

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
            persistModel()
            startClock()
        }

        rerender()
    }

    private fun restoreInstanceState(savedInstanceState: Bundle) {
        targetTime = LocalTime.parse(savedInstanceState.getString(PREF_KEY_TARGET_TIME))
        preTargetMinutes = savedInstanceState.getInt(PREF_KEY_PRE_TARGET_MINUTES)
        initialBgColor = savedInstanceState.getInt(PREF_KEY_INITIAL_BG_COLOR)
        preTargetBgColor = savedInstanceState.getInt(PREF_KEY_PRE_TARGET_BG_COLOR)
        targetBgColor = savedInstanceState.getInt(PREF_KEY_TARGET_BG_COLOR)
        dialColor = savedInstanceState.getInt(PREF_KEY_DIAL_COLOR)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PREF_KEY_TARGET_TIME, targetTime.format(DateTimeFormatter.ISO_LOCAL_TIME))
        outState.putInt(PREF_KEY_PRE_TARGET_MINUTES, preTargetMinutes)
        outState.putInt(PREF_KEY_INITIAL_BG_COLOR, initialBgColor)
        outState.putInt(PREF_KEY_PRE_TARGET_BG_COLOR, preTargetBgColor)
        outState.putInt(PREF_KEY_TARGET_BG_COLOR, targetBgColor)
        outState.putInt(PREF_KEY_DIAL_COLOR, dialColor)
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
        val prefs = getPreferences(MODE_PRIVATE)

        targetTime = LocalTime.parse(prefs.getString(PREF_KEY_TARGET_TIME, "10:00"))
        preTargetMinutes = prefs.getInt(PREF_KEY_PRE_TARGET_MINUTES, 5)

        initialBgColor = prefs.getInt(PREF_KEY_INITIAL_BG_COLOR, "#a60020".toColorInt())
        preTargetBgColor = prefs.getInt(PREF_KEY_PRE_TARGET_BG_COLOR, "#d88000".toColorInt())
        targetBgColor = prefs.getInt(PREF_KEY_TARGET_BG_COLOR, "#168600".toColorInt())
        dialColor = prefs.getInt(PREF_KEY_DIAL_COLOR, "#acacac".toColorInt())
    }

    private fun persistModel() {
        getPreferences(MODE_PRIVATE).edit()
            .putString(PREF_KEY_TARGET_TIME, targetTime.format(DateTimeFormatter.ISO_LOCAL_TIME))
            .putInt(PREF_KEY_PRE_TARGET_MINUTES, preTargetMinutes)
            .putInt(PREF_KEY_INITIAL_BG_COLOR, initialBgColor)
            .putInt(PREF_KEY_PRE_TARGET_BG_COLOR, preTargetBgColor)
            .putInt(PREF_KEY_TARGET_BG_COLOR, targetBgColor)
            .putInt(PREF_KEY_DIAL_COLOR, dialColor)
            .apply()
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
        Log.d("START", "initialBg=${initialBgColor.toUInt().toString(16)}")
        Log.d("START", "dial=${dialColor.toUInt().toString(16)}")
        Log.d("START", "preTarget=${preTargetBgColor.toUInt().toString(16)}")
        Log.d("START", "target=${targetBgColor.toUInt().toString(16)}")

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
