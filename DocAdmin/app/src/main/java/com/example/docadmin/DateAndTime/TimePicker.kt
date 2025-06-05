package com.example.docadmin.DateAndTime

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalTime

class TimePickerActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val timePicker = TimePickerDialog(this, { _, hourOfDay, minute ->
            val selectedTime = LocalTime.of(hourOfDay, minute)
            val resultIntent = Intent().apply {
                putExtra("selectedTime", selectedTime)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }, 9, 0, false) // Default 9:00 AM

        timePicker.show()
    }
}
