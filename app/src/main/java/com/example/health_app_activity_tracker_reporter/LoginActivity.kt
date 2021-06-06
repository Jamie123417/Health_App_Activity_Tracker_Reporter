package com.example.health_app_activity_tracker_reporter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private lateinit var entEmailUserName: EditText
    private lateinit var entPassword: EditText
    private lateinit var btnLogin : Button
    private lateinit var btnSignUp : Button
    private lateinit var scrollLogin : ScrollView
    private lateinit var databaseResources: DatabaseResources

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        entEmailUserName = findViewById(R.id.entEmailName)
        entPassword = findViewById(R.id.entPassword)
        btnLogin = findViewById(R.id.btn_LogIn)
        btnSignUp = findViewById(R.id.btn_Register)
        scrollLogin = findViewById(R.id.scrollViewLogin)

        databaseResources = DatabaseResources(applicationContext)

        btnLogin.setOnClickListener  {
            if (validInput() ) {
                if(!databaseResources.registerCheckUserEmail("admin@email.com")){
                    databaseResources.addAdmin()
                }
                val emailUserName = entEmailUserName.text.toString()
                val password = entPassword.text.toString()
                verifySQ(emailUserName, password)
            }
        }
        btnSignUp.setOnClickListener {
            val intentRegistration = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intentRegistration)
        }
    }

    private fun verifySQ(emailUserName: String, password: String) {
        if (databaseResources.loginCheckUserName(emailUserName, password)) {
            val intentLogin = Intent(applicationContext, Homepage::class.java)
            intentLogin.putExtra("USERNAME", emailUserName)
            Snackbar.make(scrollLogin, getString(R.string.login_message), Snackbar.LENGTH_LONG).show()
            startActivity(intentLogin)
        } else if (databaseResources.loginCheckUserEmail(emailUserName, password)) {
            val intentLogin = Intent(applicationContext, Homepage::class.java)
            intentLogin.putExtra("EMAIL", emailUserName)
            Snackbar.make(scrollLogin, getString(R.string.login_message), Snackbar.LENGTH_LONG).show()
            startActivity(intentLogin)
        } else {
            Snackbar.make(scrollLogin, getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG).show()
          return
        }
    }

    private fun validInput(): Boolean {
        val minPassLen = 5
        if (entEmailUserName.text.toString() == "") {
            Snackbar.make(scrollLogin, getString(R.string.enter_your_email), Snackbar.LENGTH_LONG).show()
            return false
        } else if (entPassword.text.toString() == "") {
            Snackbar.make(scrollLogin, getString(R.string.enter_your_password), Snackbar.LENGTH_LONG).show()
            return false
        } else if (entPassword.text.toString() == "") {
            Snackbar.make(scrollLogin, getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG).show()
            return false
        } else if (entPassword.text.length < minPassLen) {
            val mess = "The Password must be more than " + minPassLen + "characters"
            Snackbar.make(scrollLogin, mess, Snackbar.LENGTH_LONG).show()
            return false
        }
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}
