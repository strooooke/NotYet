package com.lambdasoup.notyet

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.time.LocalTime

class TimePickerFragment  : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private lateinit var onResultCallback: (LocalTime) -> Unit
    private lateinit var initialValue: LocalTime

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val hour = initialValue.hour
        val minute = initialValue.minute

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        onResultCallback(LocalTime.of(hourOfDay, minute))
    }

    fun withInitialValue(initialValue: LocalTime): TimePickerFragment {
        this.initialValue = initialValue
        return this
    }

    fun withCallback(callback: (LocalTime) -> Unit): TimePickerFragment {
        onResultCallback = callback
        return this
    }
}
