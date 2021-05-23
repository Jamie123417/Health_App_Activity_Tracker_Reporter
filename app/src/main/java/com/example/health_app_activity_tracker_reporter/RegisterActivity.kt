package com.example.health_app_activity_tracker_reporter

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    lateinit var userName: EditText
    lateinit var firstName: EditText
    lateinit var surname: EditText
    lateinit var emailAddress:EditText
    lateinit var password1:EditText
    lateinit var password2:EditText
    private val minPassLen = 6
    private lateinit var btnRegister : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Register"

        btnRegister = findViewById(R.id.reg_btn_register)

        userName = findViewById(R.id.regUserName)
        firstName = findViewById(R.id.regFirstName)
        surname = findViewById(R.id.regSurname)
        emailAddress = findViewById(R.id.regEmailAddress)
        password1 = findViewById(R.id.regPassword1)
        password2 = findViewById(R.id.regPassword2)

        btnRegister.setOnClickListener{
            if (validateInput()) {
                // Input is valid, here send data to your server
                val Username: String = userName.getText().toString()
                val firstName: String = firstName.getText().toString()
                val surname: String = surname.getText().toString()
                val email: String = emailAddress.getText().toString()
                val password: String = password1.getText().toString()
                val repeatPassword: String = password2.getText().toString()
                Toast.makeText(this, "entRepeatPassword Success", Toast.LENGTH_SHORT).show()
                // Here you can call you API
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    // Checking if the input in form is valid
    fun validateInput(): Boolean {
        if (userName.text.toString().equals("")) {
            userName.setError("Please Enter User Name")
            return false
        }
        if (firstName.text.toString().equals("")) {
            firstName.setError("Please Enter Your First Name")
            return false
        }
        if (surname.text.toString().equals("")) {
            surname.setError("Please Enter Your Surname")
            return false
        }
        if (emailAddress.text.toString().equals("")) {
            emailAddress.setError("Please Enter Email")
            return false
        }
        if (password1.text.toString().equals("")) {
            password1.setError("Please Enter Password")
            return false
        }
        if (password2.text.toString().equals("")) {
            password2.setError("Please Enter Repeat Password")
            return false
        }

        // checking the proper email format
        if (!isEmailValid(emailAddress.text.toString())) {
            emailAddress.setError("Please Enter Valid Email")
            return false
        }

        // checking minimum password Length
        if (password1.text.length < minPassLen) {
            password1.setError("Password Length must be more than " + minPassLen + "characters")
            return false
        }

        // Checking if repeat password is same
        if (!password1.text.toString().equals(password2.text.toString())) {
            password2.setError("Password does not match")
            return false
        }
        return true
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
/*
    fun register(v: View?) {
        if (validateInput()) {
            // Input is valid, here send data to your server
            val Username: String = userName.getText().toString()
            val firstName: String = firstName.getText().toString()
            val surname: String = surname.getText().toString()
            val email: String = emailAddress.getText().toString()
            val password: String = password1.getText().toString()
            val repeatPassword: String = password2.getText().toString()
            Toast.makeText(this, "entRepeatPassword Success", Toast.LENGTH_SHORT).show()
            // Here you can call you API
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
    */
}

