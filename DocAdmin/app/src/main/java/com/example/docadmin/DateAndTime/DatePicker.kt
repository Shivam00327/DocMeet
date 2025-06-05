package com.example.docadmin.DateAndTime

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate

class DatePickerActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            val resultIntent = Intent().apply {
                putExtra("selectedDate", selectedDate)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }, LocalDate.now().year, LocalDate.now().monthValue - 1, LocalDate.now().dayOfMonth)

        datePicker.show()
    }
}
