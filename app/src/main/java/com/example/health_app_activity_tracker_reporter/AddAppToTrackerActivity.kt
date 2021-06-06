package com.example.health_app_activity_tracker_reporter

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class AddAppToTrackerActivity : AppCompatActivity() {
    private lateinit var appNameSpinner: Spinner
    private lateinit var weeks:EditText
    private lateinit var days:EditText
    private lateinit var hours:EditText
    private lateinit var btnTrackApp : Button
    private lateinit var databaseResources: DatabaseResources

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_app_to_tracker)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        databaseResources = DatabaseResources(applicationContext)

        val appListInstance = AppsActivity()
        val installedAppsList: MutableList<AppList> = appListInstance.getInstalledApps()

        appNameSpinner = findViewById(R.id.selectAppName)
        weeks = findViewById(R.id.regWeeks)
        days = findViewById(R.id.regDays)
        hours = findViewById(R.id.regHours)

        // spinner
        val spinnerArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, installedAppsList)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        appNameSpinner.adapter = spinnerArrayAdapter

        //button
        btnTrackApp.setOnClickListener{
            if (validInput()) {
                val appName = appNameSpinner.getSelectedItem().toString()
                val weeks = weeks.text.toString()
                val days = days.text.toString()
                val hours = hours.text.toString()
                val concatenate = (weeks + days + hours)
                val interval: Int = concatenate.toInt()
                for (i in installedAppsList.indices){
                    val insAppName = installedAppsList[i].appName
                    val dateLastUsed = installedAppsList[i].dateLastUsed
                    if (insAppName == appName) {
                        if(databaseResources.checkAppTracking(appName)){
                            val appPackages = installedAppsList[i].appPackages
                            databaseResources.addTracker(Tracker(0, appName, appPackages, dateLastUsed, interval))

                        }
                    }
                }
                Toast.makeText(this, "App Successfully Added to Tracked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, TrackerActivity::class.java)
                startActivity(intent)
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun validInput(): Boolean {
        if ((weeks.text.toString().toInt() + days.text.toString().toInt() + hours.text.toString().toInt()) != 0) {
            return true
        } else if (appNameSpinner.getSelectedItem().toString() != "") {
            return true
        } else {
            return false
        }
    }

}