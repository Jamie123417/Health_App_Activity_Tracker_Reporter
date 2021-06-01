package com.example.health_app_activity_tracker_reporter

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {

    lateinit var entUserName: EditText
    lateinit var entPassword: EditText
    private val adminUserName = "admin"
    private val adminPassword = "admin"
    private lateinit var btmLogin : Button
    private lateinit var btmRegister : Button
    private val MIN_PASSWORD_LENGTH = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Login"

        entUserName = findViewById(R.id.entUserName)
        entPassword = findViewById(R.id.entPassword)
        btmLogin = findViewById(R.id.btn_LogIn)
        btmRegister = findViewById(R.id.btn_Register)

        btmLogin.setOnClickListener() {
            if (validInput()) {
                // Input is valid, here send data to your server
                val email: String = entUserName.text.toString()
                val password: String = entPassword.text.toString()
                Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Homepage::class.java)
                startActivity(intent)
            } else {
                // for quick testing purposes - remove before release
                if (entUserName.text.toString() == adminUserName && entPassword.getText().toString() == adminPassword) {
                    Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Homepage::class.java)
                    startActivity(intent)
                }
            }
        }

        // goto register page
        btmRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

        private fun validInput(): Boolean {
            if (entUserName.text.toString() == "") {
                entUserName.error = "Please Enter UserName"
                return false
            }
            if (entPassword.text.toString() == "") {
                entPassword.error = "Please Enter Password"
                return false
            }

            // checking minimum password Length
            if (entPassword.text.length < MIN_PASSWORD_LENGTH) {
                entPassword.error = "Password Length must be more than " + MIN_PASSWORD_LENGTH + "characters"
                return false
            }
            return true
        }
}
