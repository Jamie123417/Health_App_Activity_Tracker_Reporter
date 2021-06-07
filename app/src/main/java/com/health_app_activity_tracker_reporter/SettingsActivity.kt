package com.health_app_activity_tracker_reporter

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.widget.Button
import com.example.health_app_activity_tracker_reporter.R
import com.health_app_activity_tracker_reporter.classes.User
import com.health_app_activity_tracker_reporter.resources.DatabaseResources

class SettingsActivity : AppCompatActivity() {
    private lateinit var textViewAccUName: TextView
    private lateinit var textViewAccEmail: TextView
    private lateinit var textViewAccFName: TextView
    private lateinit var textViewAccLName: TextView
    private lateinit var buttonDelAccount: Button

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
        buttonDelAccount = findViewById(R.id.btn_del_account)

        val userEmail = intent.getStringExtra("EMAIL").toString()
        val userName = intent.getStringExtra("USERNAME").toString()
        databaseResources = DatabaseResources(this)

        if (userEmail != "null") {
            userDetails = databaseResources.findUserDetailsEmail(userEmail)!!
            textViewAccUName.text = ("Username: " + userDetails.userName)
            textViewAccEmail.text = ("User Email: " + userDetails.email)
            textViewAccFName.text = ("User First Name: " + userDetails.firstName)
            textViewAccLName.text = ("User Last Name: " + userDetails.lastName)
        } else if (userName != "null") {
            userDetails = databaseResources.findUserDetailsUserName(userName)!!
            textViewAccUName.text = ("Username: " + userDetails.userName)
            textViewAccEmail.text = ("User Email: " + userDetails.email)
            textViewAccFName.text = ("User First Name: " + userDetails.firstName)
            textViewAccLName.text = ("User Last Name: " + userDetails.lastName)
        }

        buttonDelAccount.setOnClickListener {
            databaseResources.deleteUser(userDetails)
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