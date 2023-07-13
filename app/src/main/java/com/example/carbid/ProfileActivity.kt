package  com.example.carbid

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {

    private lateinit var userEmailTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var passwordTextView: TextView
    private lateinit var userRef: DatabaseReference

    companion object {
        private const val TAG = "ProfileActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        userEmailTextView = findViewById(R.id.emailTextView)
        usernameTextView = findViewById(R.id.usernameTextView)

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val userId = user.uid
            userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val username = dataSnapshot.child("username").value as? String
                    val password = dataSnapshot.child("password").value as? String

                    userEmailTextView.text = user.email
                    usernameTextView.text = username
                    passwordTextView.text = password
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error if needed
                    Log.e(TAG, "Error retrieving user data: ${databaseError.message}")
                    Toast.makeText(this@ProfileActivity, "Error retrieving user data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
