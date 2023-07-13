package com.example.carbid

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var progress: ProgressDialog
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val mEmail = findViewById<EditText>(R.id.email)
        val mUser = findViewById<EditText>(R.id.username)
        val mPass = findViewById<EditText>(R.id.password)
        val mRPass = findViewById<EditText>(R.id.rpassword)
        val mCreate = findViewById<ImageButton>(R.id.signupbtn)
        val mLogin = findViewById<Button>(R.id.signinbtn)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        progress = ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait....")

        mCreate.setOnClickListener {
            // Start by receiving data from the user
            val email = mEmail.text.toString().trim()
            val password = mPass.text.toString().trim()
            val rpassword = mRPass.text.toString().trim()
            val user = mUser.text.toString().trim()

            // Check if the user is submitting empty fields
            if (email.isEmpty()) {
                mEmail.error = "Please fill this input"
                mEmail.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                mPass.error = "Please fill this input"
                mPass.requestFocus()
                return@setOnClickListener
            }
            if (user.isEmpty()) {
                mUser.error = "Please fill this input"
                mUser.requestFocus()
                return@setOnClickListener
            }
            if (rpassword.isEmpty()) {
                mRPass.error = "Please fill this input"
                mRPass.requestFocus()
                return@setOnClickListener
            }
            if (rpassword != password) {
                mRPass.error = "Passwords do not match!"
                return@setOnClickListener
            }
            if (password.length < 6) {
                mPass.error = "Password must be at least 6 characters long!"
                mPass.requestFocus()
                return@setOnClickListener
            }

            // Proceed to register the user
            progress.show()
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                progress.dismiss()
                if (task.isSuccessful) {
                    val userId = mAuth.currentUser?.uid
                    if (userId != null) {
                        saveUsernameToDatabase(userId, user, email)
                    }
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    displayMessage("ERROR", task.exception?.message.toString())
                }
            }
        }

        mLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun displayMessage(title: String, message: String) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setPositiveButton("OK", null)
        alertDialog.create().show()
    }

    private fun saveUsernameToDatabase(userId: String, username: String, email: String) {
        val usersRef = database.reference.child("users")
        val userRef = usersRef.child(userId)

        val userData = HashMap<String, Any>()
        userData["username"] = username
        userData["email"] = email

        userRef.setValue(userData)
            .addOnSuccessListener {
               displayMessage("Success", "Username saved")
            }
            .addOnFailureListener {
                displayMessage("Error", "Failed to save username and email to the database")
            }
    }
}
