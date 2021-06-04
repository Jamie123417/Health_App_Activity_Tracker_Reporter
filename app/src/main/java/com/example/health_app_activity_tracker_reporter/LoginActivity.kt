package com.example.health_app_activity_tracker_reporter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity() {

    private lateinit var entEmail: EditText
    private lateinit var entPassword: EditText
    private lateinit var btnLogin : Button
    private lateinit var btnSignUp : Button

    private lateinit var databaseResources: DatabaseResources

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        entEmail = findViewById(R.id.entEmailName)
        entPassword = findViewById(R.id.entPassword)
        btnLogin = findViewById(R.id.btn_LogIn)
        btnSignUp = findViewById(R.id.btn_Register)

        databaseResources = DatabaseResources(this)

        btnLogin.setOnClickListener  {
            verifySQ(databaseResources)
        }

        btnSignUp.setOnClickListener {
            val intentRegistration = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intentRegistration)
        }

    }

    private fun verifySQ(databaseResources: DatabaseResources) {
        if (!validInput()) {
            return
        }
        if (databaseResources.checkUser(entEmail.text.toString().trim { it <= ' ' })) {
            val intentLogin = Intent(applicationContext, Homepage::class.java)
            intentLogin.putExtra("EMAIL", entEmail.text.toString().trim { it <= ' ' })
            startActivity(intentLogin)
        } else {
            Toast.makeText(this, "@string/error_valid_email_password", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validInput(): Boolean {
        val minPassLen = 5
        if (entEmail.text.toString() == "") {
            entEmail.error = "@string/error_message_email"
            return false
        } else if (entPassword.text.toString() == "") {
            entPassword.error = "@string/error_valid_email_password"
            return false
        } else if (entPassword.text.length < minPassLen) {
            entPassword.error = "The Password must be more than " + minPassLen + "characters"
            return false
        } else if (!isEmailValid(entEmail.text.toString())){
            return false
        }
        return true
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
