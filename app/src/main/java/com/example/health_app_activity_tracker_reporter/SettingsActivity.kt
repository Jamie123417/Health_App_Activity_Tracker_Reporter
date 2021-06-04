package com.example.health_app_activity_tracker_reporter

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    private lateinit var listViewAccDetails: ListView
    private lateinit var textViewAccUName: TextView
    private lateinit var textViewAccEmail: TextView
    private lateinit var textViewAccFName: TextView
    private lateinit var textViewAccLName: TextView

    private lateinit var userDetails: User
    private lateinit var databaseResources: DatabaseResources

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Settings"

        listViewAccDetails = findViewById(R.id.settings_scroll_view)
        textViewAccUName = findViewById(R.id.settings_user_name)
        textViewAccEmail = findViewById(R.id.settings_email)
        textViewAccFName = findViewById(R.id.settings_first_name)
        textViewAccLName = findViewById(R.id.settings_last_name)

        val userEmail = intent.getStringExtra("email").toString()

        databaseResources = DatabaseResources(this)
        userDetails = databaseResources.getUserDetails(userEmail)

        listViewAccDetails.setOnItemClickListener { parent, view, position, id ->
                textViewAccUName.text = userDetails.userName
                textViewAccEmail.text = userDetails.email
                textViewAccFName.text = userDetails.firstName
                textViewAccLName.text = userDetails.lastName
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                startActivity(intent)
        }
    }

/*    private fun setUserDetails(userDetails: List<User>) {
        if (userDetails.email == ) {
            val intentLogin = Intent(applicationContext, Homepage::class.java)
            intentLogin.putExtra("EMAIL", entEmail.text.toString().trim { it <= ' ' })
            startActivity(intentLogin)
        }
    }*/

}