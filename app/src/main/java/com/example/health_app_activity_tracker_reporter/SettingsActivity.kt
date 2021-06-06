package com.example.health_app_activity_tracker_reporter

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.R.menu
import android.view.Menu

class SettingsActivity : AppCompatActivity() {
    private lateinit var textViewAccUName: TextView
    private lateinit var textViewAccEmail: TextView
    private lateinit var textViewAccFName: TextView
    private lateinit var textViewAccLName: TextView

    private lateinit var databaseResources: DatabaseResources
    private lateinit var userDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        textViewAccUName = findViewById(R.id.settings_user_name)
        textViewAccEmail = findViewById(R.id.settings_email)
        textViewAccFName = findViewById(R.id.settings_first_name)
        textViewAccLName = findViewById(R.id.settings_last_name)

        val userEmail = intent.getStringExtra("EMAIL").toString()
        val userName = intent.getStringExtra("PASSWORD").toString()

        if (userEmail != "") {
            databaseResources = DatabaseResources(this)
            userDetails = databaseResources.findUserDetailsEmail(userEmail)!!
            textViewAccUName.text = userDetails.userName
            textViewAccEmail.text = userDetails.email
            textViewAccFName.text = userDetails.firstName
            textViewAccLName.text = userDetails.lastName
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            startActivity(intent)
        } else if (userName != "") {
            databaseResources = DatabaseResources(this)
            userDetails = databaseResources.findUserDetailsUserName(userName)!!
            textViewAccUName.text = userDetails.userName
            textViewAccEmail.text = userDetails.email
            textViewAccFName.text = userDetails.firstName
            textViewAccLName.text = userDetails.lastName
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            startActivity(intent)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }
}