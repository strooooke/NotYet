package com.lambdasoup.notyet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.google.android.material.button.MaterialButton
import java.time.LocalDateTime

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_start)

        findViewById<MaterialButton>(R.id.start_button).setOnClickListener {
            startActivity(
                ClockActivity.getIntent(
                    applicationContext,
                    closeToTargetTime = LocalDateTime.now().plusMinutes(1),
                    targetTime = LocalDateTime.now().plusMinutes(2),
                    dialColor = "#FF888888".toColorInt(),
                    farFromTargetBgColor = "#FF00FF88".toColorInt(),
                    closeToTargetBgColor = "#FFAAAA00".toColorInt(),
                    targetBgColor = "#FFAA00AA".toColorInt(),
                )
            )
        }
    }
}
