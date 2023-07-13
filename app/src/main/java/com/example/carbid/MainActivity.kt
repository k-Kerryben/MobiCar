package com.example.carbid

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    lateinit var progress: ProgressDialog
    lateinit var mAuth: FirebaseAuth
    lateinit var database: FirebaseDatabase

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mEmail = findViewById<EditText>(R.id.email)
        val mpass = findViewById<EditText>(R.id.password)
        val login = findViewById<ImageButton>(R.id.loginbtn)
        val mcreate = findViewById<Button>(R.id.btnsignup)
        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        progress.setOnCancelListener {

        }

        mcreate.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        login.setOnClickListener {
            // start by receiving data from user
            val emailOrUsername = mEmail.text.toString().trim()
            val password = mpass.text.toString().trim()

            // check if user is submitting empty fields
            if (emailOrUsername.isEmpty()) {
                mEmail.error = "Please fill this input"
                mEmail.requestFocus()
            } else if (password.isEmpty()) {
                mpass.error = "Please fill this input"
                mpass.requestFocus()
            } else {
                // proceed to authenticate the user
                progress.show()

                // Check if the user entered an email or username
                if (emailOrUsername.contains('@')) {
                    // Email login
                    mAuth.signInWithEmailAndPassword(emailOrUsername, password)
                        .addOnCompleteListener { task ->
                            progress.dismiss()
                            if (task.isSuccessful) {
                                saveLoginState()
                                Toast.makeText(this, "Successful Login", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                            } else {
                                displayMessage("Error", task.exception?.message.toString())
                            }
                        }
                } else {
                    // Username login
                    val usersRef = database.reference.child("users")
                    val query = usersRef.orderByChild("username").equalTo(emailOrUsername)

                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            progress.dismiss()
                            if (dataSnapshot.exists()) {
                                val userId = dataSnapshot.children.first().key
                                if (userId != null) {
                                    val userRef = usersRef.child(userId)
                                    val userEmail = userRef.child("email").toString()

                                    mAuth.signInWithEmailAndPassword(userEmail, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                saveLoginState()
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "Successful Login",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                startActivity(
                                                    Intent(this@MainActivity, HomeActivity::class.java
                                                    )
                                                )
                                                finish()
                                            } else {
                                                displayMessage(
                                                    "Error",
                                                    task.exception?.message.toString()
                                                )
                                            }
                                        }
                                } else {
                                    displayMessage("Error", "User ID not found")
                                }
                            } else {
                                displayMessage("Error", "Username not found")
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            progress.dismiss()
                            displayMessage("Error", databaseError.message)
                        }
                    })
                }
            }
        }
    }

    private fun saveLoginState() {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }

    private fun displayMessage(title: String, message: String) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton("OK", null)
        alertDialog.create().show()
    }
}
