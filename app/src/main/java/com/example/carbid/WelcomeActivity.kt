package com.example.carbid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.widget.Button


class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val login = findViewById<Button>(R.id.login_button)
        val register = findViewById<Button>(R.id.register_button)


        login.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
        register.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Check login state in SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

// Redirect the user based on the login state
        if (isLoggedIn) {
            // User is already logged in, navigate to the appropriate screen
            // For example, you can start a HomeActivity
            startActivity(Intent(this, HomeActivity::class.java))
            finish() // Optional: Close the LoginActivity to prevent going back to it
        } else {
            // User is not logged in, stay on the LoginActivity
            // or redirect them to the login screen
            startActivity(Intent(this, MainActivity::class.java))
        }

    }
}