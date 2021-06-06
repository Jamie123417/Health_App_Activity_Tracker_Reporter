package com.example.health_app_activity_tracker_reporter

import android.R.menu
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.Menu
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar


class RegisterActivity : AppCompatActivity() {
    private lateinit var regUserName: EditText
    private lateinit var regFirstName: EditText
    private lateinit var regLastName: EditText
    private lateinit var regEmailAddress:EditText
    private lateinit var regPassword1:EditText
    private lateinit var regPassword2:EditText
    private lateinit var btnRegister : Button
    private val minPassLen = 5
    private lateinit var scrollRegister : ScrollView
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
        scrollRegister = findViewById(R.id.scrollViewRegister)

        databaseResources = DatabaseResources(this)

        btnRegister.setOnClickListener {
            if (validateInput()) {
                postDataToSQLite()
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }
    private fun postDataToSQLite() {
        if (!databaseResources.registerCheckUser(regEmailAddress.text.toString())) {
            val user = User(1, "temp", "temp", "temp", "temp", "temp")
            user.id = (databaseResources.getUsersdbSize() + 1)
            user.userName = regUserName.text.toString().trim()
            user.firstName = regUserName.text.toString().trim()
            user.lastName = regUserName.text.toString().trim()
            user.email = regEmailAddress.text.toString().trim()
            user.password = regPassword1.text.toString().trim()
            databaseResources.addUser(user)
            Snackbar.make(scrollRegister, getString(R.string.success_message), Snackbar.LENGTH_LONG).show()
            val intentRegister = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intentRegister)
        } else {
            Snackbar.make(scrollRegister, getString(R.string.error_email_exists), Snackbar.LENGTH_LONG).show()
        }
    }

    // Checking if the input in form is valid
    private fun validateInput(): Boolean {
        if (regUserName.text.toString() == "") {
//            regUserName.error = ("Please Enter User Name")
            Snackbar.make(scrollRegister, getString(R.string.hint_name), Snackbar.LENGTH_LONG).show()
            return false
        }
        if (regFirstName.text.toString() == "") {
//            regFirstName.error = ("Please Enter Your First Name")
            Snackbar.make(scrollRegister, getString(R.string.hint_first_name), Snackbar.LENGTH_LONG).show()
            return false
        }
        if (regLastName.text.toString() == "") {
//            regLastName.error = ("Please Enter Your Surname")
            Snackbar.make(scrollRegister, getString(R.string.hint_last_name), Snackbar.LENGTH_LONG).show()
            return false
        }
        if (regEmailAddress.text.toString() == "") {
//            regEmailAddress.error = ("Please Enter Email")
            Snackbar.make(scrollRegister, getString(R.string.hint_email), Snackbar.LENGTH_LONG).show()
            return false
        }
        if (regPassword1.text.toString() == "") {
//            regPassword1.error = ("Please Enter Password")
            Snackbar.make(scrollRegister, getString(R.string.hint_password), Snackbar.LENGTH_LONG).show()
            return false
        }
        if (regPassword2.text.toString() == "") {
//            regPassword2.error = ("Please Enter Repeat Password")
            Snackbar.make(scrollRegister, getString(R.string.hint_confirm_password), Snackbar.LENGTH_LONG).show()
            return false
        }
        // checking the proper email format
        if (!isEmailValid(regEmailAddress.text.toString())) {
//            regEmailAddress.error = ("Please Enter Valid Email")
            Snackbar.make(scrollRegister, getString(R.string.valid_email), Snackbar.LENGTH_LONG).show()
            return false
        }
        // checking minimum password Length
        if (regPassword1.text.length < minPassLen) {
//            regPassword1.error = ("Password Length must be more than " + minPassLen + "characters")
            val len = "Password Length must be more than " + minPassLen + "characters"
            Snackbar.make(scrollRegister, len, Snackbar.LENGTH_LONG).show()
            return false
        }
        // Checking if repeat password is same
        if (regPassword1.text.toString() != regPassword2.text.toString()) {
//            regPassword2.error = "Password does not match"
            Snackbar.make(scrollRegister, getString(R.string.error_password_match), Snackbar.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    //Closes the keyboard on click
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}