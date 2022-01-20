package com.lambdasoup.notyet

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.ColorInt
import java.time.LocalDateTime

class ClockActivity : AppCompatActivity() {
    private lateinit var deviceAdmin: DeviceAdmin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
            // set via manifest attributes if API level high enough
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        }

        deviceAdmin = DeviceAdmin(applicationContext)

        findViewById<ClockView>(R.id.clock).apply {
            (intent.getSerializableExtra(CLOSE_TO_TARGET_TIME) as LocalDateTime?)?.let {
                closeToTargetTime = it
            }
            (intent.getSerializableExtra(TARGET_TIME) as LocalDateTime?)?.let {
                targetTime = it
            }
            (intent.getIntExtra(DIAL_COLOR, -1)).takeIf { it != -1 }?.let {
                dialColor = it
            }
            (intent.getIntExtra(FAR_FROM_TARGET_BG_COLOR, -1)).takeIf { it != -1 }?.let {
                farFromTargetBackgroundColor = it
            }
            (intent.getIntExtra(CLOSE_TO_TARGET_BG_COLOR, -1)).takeIf { it != -1 }?.let {
                closeToTargetBackgroundColor = it
            }
            (intent.getIntExtra(TARGET_BG_COLOR, -1)).takeIf { it != -1 }?.let {
                targetBackgroundColor = it
            }
        }
    }

    override fun onBackPressed() {
        deviceAdmin.lockScreen()
        super.onBackPressed()
    }

    companion object {
        private const val CLOSE_TO_TARGET_TIME = "com.lambdasoup.notyet.ClockActivity_EXTRA_closeToTargetTime"
        private const val TARGET_TIME = "com.lambdasoup.notyet.ClockActivity_EXTRA_targetTime"
        private const val DIAL_COLOR = "com.lambdasoup.notyet.ClockActivity_EXTRA_dialColor"
        private const val FAR_FROM_TARGET_BG_COLOR = "com.lambdasoup.notyet.ClockActivity_EXTRA_farFromTargetBgColor"
        private const val CLOSE_TO_TARGET_BG_COLOR = "com.lambdasoup.notyet.ClockActivity_EXTRA_closeToTargetBgColor"
        private const val TARGET_BG_COLOR = "com.lambdasoup.notyet.ClockActivity_EXTRA_targetBgColor"

        fun getIntent(
            context: Context, closeToTargetTime: LocalDateTime? = null, targetTime: LocalDateTime? = null,
            @ColorInt dialColor: Int = -1, @ColorInt farFromTargetBgColor: Int = -1, @ColorInt closeToTargetBgColor: Int = -1,
            @ColorInt targetBgColor: Int = -1
        ): Intent =
            Intent(context, ClockActivity::class.java)
                .putExtra(CLOSE_TO_TARGET_TIME, closeToTargetTime)
                .putExtra(TARGET_TIME, targetTime)
                .putExtra(DIAL_COLOR, dialColor)
                .putExtra(FAR_FROM_TARGET_BG_COLOR, farFromTargetBgColor)
                .putExtra(CLOSE_TO_TARGET_BG_COLOR, closeToTargetBgColor)
                .putExtra(TARGET_BG_COLOR, targetBgColor)
    }
}
