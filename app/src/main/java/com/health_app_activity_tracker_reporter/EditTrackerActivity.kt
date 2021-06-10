package com.health_app_activity_tracker_reporter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import com.example.health_app_activity_tracker_reporter.R
import com.health_app_activity_tracker_reporter.classes.Tracker
import com.health_app_activity_tracker_reporter.classes.User
import com.health_app_activity_tracker_reporter.resources.DatabaseResources

class EditTrackerActivity : AppCompatActivity() {
    private lateinit var textViewAppUName: TextView
    private lateinit var textViewWeeks: TextView
    private lateinit var textViewDays: TextView
    private lateinit var textViewHours: TextView
    private lateinit var editTextWeeks: EditText
    private lateinit var editTextDays: EditText
    private lateinit var editTextHours: EditText
    private lateinit var buttonEdit: Button

    private lateinit var databaseResources: DatabaseResources
    private lateinit var trackerDetails: Tracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_tracker)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        textViewAppUName = findViewById(R.id.edit_tracker_app_name)
        textViewWeeks = findViewById(R.id.edit_tracker_weeks)
        textViewDays = findViewById(R.id.edit_tracker_days)
        textViewHours = findViewById(R.id.edit_tracker_hours)
        buttonEdit = findViewById(R.id.btn_edit_tracker)
        editTextWeeks = findViewById(R.id.new_tracker_weeks)
        editTextDays = findViewById(R.id.new_tracker_days)
        editTextHours = findViewById(R.id.new_tracker_hours)

        val appName = intent.getStringExtra("APPNAME").toString()
        databaseResources = DatabaseResources(this)

        if (appName!= "null") {
            trackerDetails = databaseResources.getAppTracker(appName)
            textViewAppUName.text = ("App Name: " + trackerDetails.appTName)
            textViewWeeks.text = ("Weeks: " + trackerDetails.appWeeks)
            textViewDays.text = ("Days: " + trackerDetails.appDays)
            textViewHours.text = ("Hours: " + trackerDetails.appHours)
        }
        buttonEdit.setOnClickListener {
            val weeks = editTextWeeks.text.toString().toIntOrNull() ?: 0
            val days = editTextDays.text.toString().toIntOrNull() ?: 0
            val hours = editTextHours.text.toString().toIntOrNull() ?: 0
            trackerDetails.appWeeks = weeks
            trackerDetails.appDays = days
            trackerDetails.appHours = hours
            databaseResources.updateTracker(trackerDetails)
            val intent = Intent(this, TrackerActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}