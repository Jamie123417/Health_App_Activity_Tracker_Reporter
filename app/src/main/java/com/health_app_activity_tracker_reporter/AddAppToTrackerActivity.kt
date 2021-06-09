package com.health_app_activity_tracker_reporter

import android.app.AppOpsManager
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import com.example.health_app_activity_tracker_reporter.R
import com.health_app_activity_tracker_reporter.classes.AppList
import com.health_app_activity_tracker_reporter.classes.Tracker
import com.health_app_activity_tracker_reporter.resources.DatabaseResources
import android.view.View
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.provider.Settings
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception
import java.util.*

class AddAppToTrackerActivity : AppCompatActivity() {
    private lateinit var appNameSpinner: Spinner
    private lateinit var weeks:EditText
    private lateinit var days:EditText
    private lateinit var hours:EditText
    private lateinit var btnTrackApp : Button
    private lateinit var databaseResources: DatabaseResources
    private lateinit var selectedApp: AppList
    private var userInsAppsList: MutableList<AppList> = ArrayList()
    private var appListName : MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_app_to_tracker)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        databaseResources = DatabaseResources(applicationContext)

        btnTrackApp = findViewById(R.id.reg_btn_track_app)
        appNameSpinner = findViewById(R.id.select_app_name)
        weeks = findViewById(R.id.reg_weeks)
        days = findViewById(R.id.reg_days)
        hours = findViewById(R.id.reg_hours)
        userInsAppsList = getInstalledApps()

        for (i in userInsAppsList.indices){
            appListName.add(userInsAppsList[i].appName)
        }

        val spinnerArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, appListName.toList())
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        appNameSpinner.adapter = spinnerArrayAdapter

        // spinner
        appNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
                selectedApp = (userInsAppsList[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        //button
        btnTrackApp.setOnClickListener  {
            if (validTrackInput()) {
                postTrackerDataToSQLite()
            }
        }
    }

    private fun postTrackerDataToSQLite() {
        val appName = appNameSpinner.selectedItem.toString()
        val weeks = weeks.text.toString().toIntOrNull() ?: 0
        val days = days.text.toString().toIntOrNull() ?: 0
        val hours = hours.text.toString().toIntOrNull() ?: 0
//        for (i in userInsAppsList.indices){
        val insAppName = selectedApp.appName
        val dateLastUsed = selectedApp.dateLastUsed
        if (insAppName == appName) {
            if(!databaseResources.checkAppTracking(appName)){
                val appPackages = selectedApp.appPackages
                databaseResources.addTracker(Tracker(0, appName, appPackages, dateLastUsed, weeks, days, hours))
                Toast.makeText(this, "App Successfully Added to Tracked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, TrackerActivity::class.java)
                startActivity(intent)
            }
//            }
        }
    }

    private fun validTrackInput(): Boolean {
        val weeks = weeks.text.toString().toIntOrNull() ?: 0
        val days = days.text.toString().toIntOrNull() ?: 0
        val hours = hours.text.toString().toIntOrNull() ?: 0
        return weeks + days + hours > 0
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

    private fun getInstalledApps(): MutableList<AppList> {
        var appsList: MutableList<AppList> = ArrayList()
        val appListPacks: List<PackageInfo> = packageManager.getInstalledPackages(0)
        for (i in appListPacks.indices) {
            val packageInfo = appListPacks[i]
            if (!isSystemPackage(packageInfo)) {
                val appName = packageInfo.applicationInfo.loadLabel(packageManager).toString()
                val icon = packageInfo.applicationInfo.loadIcon(packageManager)
                val packages = packageInfo.applicationInfo.packageName
                val dateLastUsed = getAppDateLastUsed(packages)
                appsList.add(AppList(appName, icon, packages, dateLastUsed))
            }
        }
        return appsList
    }

    private fun isSystemPackage(pkgInfo: PackageInfo): Boolean {
        return pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    private fun getAppDateLastUsed(packageName: String): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -1)
        val usageStatsManager : UsageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val customUsageStats: List<UsageStats> = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, cal.timeInMillis, System.currentTimeMillis())
        for (i in customUsageStats.indices) {
            val pacName = customUsageStats[i].packageName.toString()
            if (packageName == pacName) {
                val dateLastUsed = customUsageStats[i].lastTimeUsed
                return (dateLastUsed)
            }
        }
        return 0
    }

}