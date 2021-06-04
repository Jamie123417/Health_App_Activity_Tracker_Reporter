package com.example.health_app_activity_tracker_reporter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.sql.DriverManager
import java.sql.ResultSet


class RegisterActivity : AppCompatActivity() {
    lateinit var regUserName: EditText
    lateinit var regFirstName: EditText
    lateinit var regLastName: EditText
    lateinit var regEmailAddress:EditText
    lateinit var regPassword1:EditText
    lateinit var regPassword2:EditText
    private lateinit var btnRegister : Button
    private val minPassLen = 5
    private lateinit var databaseResources: DatabaseResources

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnRegister = findViewById(R.id.reg_btn_add_app)
        regUserName = findViewById(R.id.regUserName)
        regFirstName = findViewById(R.id.regFirstName)
        regLastName = findViewById(R.id.regLastName)
        regEmailAddress = findViewById(R.id.regEmail)
        regPassword1 = findViewById(R.id.regPassword1)
        regPassword2 = findViewById(R.id.regPassword2)

        databaseResources = DatabaseResources(this)

        btnRegister.setOnClickListener {
            when (it.id) {
                R.id.btn_Register -> postDataToSQLite()
            }
        }
    }

    private fun postDataToSQLite() {
        if (!validateInput()) {
            return
        }
        if (!databaseResources.checkUser(regEmailAddress.text.toString().trim { it <= ' ' }) ) {
            val user = User(0, "temp", "temp", "temp", "temp", "temp")
            user.id = (databaseResources.getDBSize() + 1)
            user.userName = regUserName.text.toString().trim { it <= ' ' }
            user.firstName = regUserName.text.toString().trim { it <= ' ' }
            user.lastName = regUserName.text.toString().trim { it <= ' ' }
            user.email = regEmailAddress.text.toString().trim { it <= ' ' }
            user.password = regPassword1.text.toString().trim { it <= ' ' }
            databaseResources.addUser(user)
            Toast.makeText(this, getString(R.string.success_message), Toast.LENGTH_SHORT).show()
            val intentRegister = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intentRegister)
        } else {
            Toast.makeText(this, getString(R.string.error_email_exists), Toast.LENGTH_SHORT).show()

        }
    }

    // Checking if the input in form is valid
    private fun validateInput(): Boolean {
        if (regUserName.text.toString().equals("")) {
            regUserName.setError("Please Enter User Name")
            return false
        }
        if (regFirstName.text.toString().equals("")) {
            regFirstName.setError("Please Enter Your First Name")
            return false
        }
        if (regLastName.text.toString().equals("")) {
            regLastName.setError("Please Enter Your Surname")
            return false
        }
        if (regEmailAddress.text.toString().equals("")) {
            regEmailAddress.setError("Please Enter Email")
            return false
        }
        if (regPassword1.text.toString().equals("")) {
            regPassword1.setError("Please Enter Password")
            return false
        }
        if (regPassword2.text.toString().equals("")) {
            regPassword2.setError("Please Enter Repeat Password")
            return false
        }
        // checking the proper email format
        if (!isEmailValid(regEmailAddress.text.toString())) {
            regEmailAddress.setError("Please Enter Valid Email")
            return false
        }
        // checking minimum password Length
        if (regPassword1.text.length < minPassLen) {
            regPassword1.setError("Password Length must be more than " + minPassLen + "characters")
            return false
        }
        // Checking if repeat password is same
        if (!regPassword1.text.toString().equals(regPassword2.text.toString())) {
            regPassword2.setError("Password does not match")
            return false
        }
        return true
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}