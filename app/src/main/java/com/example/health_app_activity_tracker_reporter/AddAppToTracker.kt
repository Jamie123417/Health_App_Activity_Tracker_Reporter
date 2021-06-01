package com.example.health_app_activity_tracker_reporter

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class AddAppToTracker : AppCompatActivity() {
    private lateinit var appNameSpinner: Spinner
    private lateinit var weeks:EditText
    private lateinit var days:EditText
    private lateinit var hours:EditText
    private lateinit var btnTrackApp : Button
    private var trackedApp: ArrayList<TrackerList> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_app_to_tracker)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Add App to Monitor"

        val appListInstance = AppsActivity()
        val installedAppsList: MutableList<AppList> = appListInstance.getInstalledApps()
        val trackerActivity = TrackerActivity()

        appNameSpinner = findViewById(R.id.selectAppName)
        weeks = findViewById(R.id.regWeeks)
        days = findViewById(R.id.regDays)
        hours = findViewById(R.id.regHours)

        // Creating adapter for spinner
        val spinnerArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, installedAppsList)
        // Drop down layout style - list view
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // attaching data adapter to spinner
        appNameSpinner.adapter = spinnerArrayAdapter

        btnTrackApp.setOnClickListener{
            if (validateInputT(installedAppsList)) {
                // Input is valid, add data to List
                var appName = appNameSpinner.getSelectedItem()
                var weeks: Int = weeks.text.toString().toInt()
                var days: Int = days.text.toString().toInt()
                var hours: Int = hours.text.toString().toInt()

                for (i in installedAppsList.indices){
                    var insAppName = installedAppsList[i].appName
                    if (insAppName == appName) {
                        var insAppIcon = installedAppsList[i].appIcon
                        var insAppPackages = installedAppsList[i].appPackages
                        var insDateLastUsed = installedAppsList[i].dateLastUsed
                        var trackingReportFreq = arrayOf(weeks, days, hours)
                        trackedApp.add(TrackerList(appName, insAppIcon, insAppPackages, insDateLastUsed, trackingReportFreq))
                    }
                }

                Toast.makeText(this, "App Successfully Added to Tracked", Toast.LENGTH_SHORT).show()
                trackerActivity.addTracker(trackedApp)
                // Call API
                val intent = Intent(this, TrackerActivity::class.java)
/*                val bundle = Bundle()
                bundle.putParcelableArrayList("appData", ArrayList(trackedApp))
                intent.putExtras(bundle)*/
                startActivity(intent)
            }
        }
    }

    private fun validateInputT(installedAppsList: MutableList<AppList>): Boolean {
        if ((weeks.text.toString().toInt() + days.text.toString().toInt() + hours.text.toString().toInt()) == 0 ) {
            return false
        }
        val appNameSpinner = appNameSpinner.getSelectedItem().toString()
//        installedAppsList.first {AppList->AppList.appName == appNameSpinner}
        val foundElement = installedAppsList.find {
            it.appName == appNameSpinner
        }
        return foundElement != null
    }

}