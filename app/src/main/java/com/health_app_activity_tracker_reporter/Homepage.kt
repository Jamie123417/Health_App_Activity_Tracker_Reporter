package com.health_app_activity_tracker_reporter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.health_app_activity_tracker_reporter.R
import com.health_app_activity_tracker_reporter.resources.DatabaseResources
import java.util.*

class Homepage : AppCompatActivity() {

    private lateinit var btnLogOut : Button
    private lateinit var textHomepageName: TextView
    private lateinit var databaseResources: DatabaseResources
    private var userEmail = ""
    private var userName = ""
    private val sharedPrefFile = "usernamesharedpreference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        userEmail = intent.getStringExtra("EMAIL").toString()
        userName = intent.getStringExtra("USERNAME").toString()

        btnLogOut = findViewById(R.id.btnLogOut)

        val btnActivitySettings = findViewById<LinearLayout>(R.id.activitySettings)
        val btnActivityApps = findViewById<LinearLayout>(R.id.activityApps)
        val btnActivityTracker = findViewById<LinearLayout>(R.id.activityTracker)
        val btnAddAppToTracker = findViewById<LinearLayout>(R.id.activityAddAppsToTracker)
        textHomepageName = findViewById(R.id.textName)

        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)

        var userFirstName : String = ""
        if (userEmail != "null") {
            databaseResources = DatabaseResources(this)
            val userDetails = databaseResources.getUserDetailsEmail(userEmail)!!
            userFirstName = userDetails.firstName.toString()

            userName = userDetails.userName.toString()

            val editor:SharedPreferences.Editor =  sharedPreferences.edit()
            editor.putString("user_name_key",userName)
            editor.apply()
            editor.commit()
        } else if (userName != "null") {
            databaseResources = DatabaseResources(this)
            val userDetails = databaseResources.getUserDetailsUserName(userName)!!
            userFirstName = userDetails.firstName.toString()

            val editor:SharedPreferences.Editor =  sharedPreferences.edit()
            editor.putString("user_name_key",userName)
            editor.apply()
            editor.commit()
        } else {
            val sharedNameValue = sharedPreferences.getString("user_name_key","defaultname")
            userName = sharedNameValue.toString()
            databaseResources = DatabaseResources(this)
            val userDetails = databaseResources.getUserDetailsUserName(userName)!!
            userFirstName = userDetails.firstName.toString()
        }
        val dt = Date()
        val c: Calendar = Calendar.getInstance()
        c.time = dt
        val hours: Int = c.get(Calendar.HOUR_OF_DAY)
        if (hours >= 1 && hours <= 12) {
            textHomepageName.text = ("Good Morning " + userFirstName)
        } else if (hours >= 12 && hours <= 16) {
            textHomepageName.text = ("Good Afternoon " + userFirstName)
        } else if (hours >= 16 && hours <= 21) {
            textHomepageName.text = ("Good Evening " + userFirstName)
        } else if (hours >= 21 && hours <= 24) {
            textHomepageName.text = ("Good Night " + userFirstName)
        }

        btnActivitySettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("EMAIL", userEmail)
            intent.putExtra("USERNAME", userName)
            startActivity(intent)
        }
        btnActivityApps.setOnClickListener {
            val intent = Intent(this, AppsActivity::class.java)
            startActivity(intent)
        }
        btnActivityTracker.setOnClickListener {
            val intent = Intent(this, TrackerActivity::class.java)
            intent.putExtra("USERNAME", userName)
            startActivity(intent)
        }
        btnAddAppToTracker.setOnClickListener {
            val intent = Intent(this, AddAppToTrackerActivity::class.java)
            intent.putExtra("USERNAME", userName)
            startActivity(intent)
        }
        btnLogOut.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }
}

