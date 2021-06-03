package com.example.health_app_activity_tracker_reporter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class Homepage : AppCompatActivity() {

    private lateinit var btnLogOut : Button
    private lateinit var textName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Homepage"

        btnLogOut = findViewById(R.id.btnLogOut)

        val btnActivitySettings = findViewById<LinearLayout>(R.id.activitySettings)
        val btnActivityApps = findViewById<LinearLayout>(R.id.activityApps)
        val btnActivityTracker = findViewById<LinearLayout>(R.id.activityTracker)
        val btnAddAppToTracker = findViewById<LinearLayout>(R.id.activityAddAppsToTracker)

        btnActivitySettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        btnActivityApps.setOnClickListener {
            val intent = Intent(this, AppsActivity::class.java)
            startActivity(intent)
        }
        btnActivityTracker.setOnClickListener {
            val intent = Intent(this, TrackerActivity::class.java)
            startActivity(intent)
        }
        btnAddAppToTracker.setOnClickListener {
            val intent = Intent(this, AddAppToTracker::class.java)
            startActivity(intent)
        }

        btnLogOut.setOnClickListener() {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        if(intent.getStringExtra("ID_EXTRA") != null){
            textName = findViewById(R.id.regFirstName)
            textName.text = intent.getStringExtra("ID_EXTRA")
        }
    }


}