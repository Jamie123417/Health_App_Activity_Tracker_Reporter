package com.example.health_app_activity_tracker_reporter

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import android.content.pm.ApplicationInfo
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView

class TrackerActivity : AppCompatActivity() {

    private var minPerHour = 60
    private var secPerMin = 60
    private var milliPerSec = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


    }


}