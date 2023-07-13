package com.example.carbid

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class PostCarActivity : AppCompatActivity() {
    private lateinit var idescription: EditText
    private lateinit var icategory: EditText
    private lateinit var imodel: EditText
    private lateinit var icontact: EditText
    private lateinit var add_img: ImageView
    private lateinit var upload_btn: Button
    private lateinit var progress: ProgressDialog
    private val PICK_IMAGE_REQUEST = 100
    private lateinit var file_path: Uri
    private lateinit var firebase_store: FirebaseStorage
    private lateinit var storage_reference: StorageReference
    private lateinit var dbref: DatabaseReference
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_car)

        idescription = findViewById(R.id.description)
        icategory = findViewById(R.id.category)
        imodel = findViewById(R.id.carmodel)
        icontact = findViewById(R.id.contact)
        add_img = findViewById(R.id.selectimagesbutton)
        upload_btn = findViewById(R.id.postbutton)
        progress = ProgressDialog(this)
        progress.setTitle("Uploading")
        progress.setMessage("Wait a minute...")
        firebase_store = FirebaseStorage.getInstance()
        storage_reference = firebase_store.reference
        fAuth = FirebaseAuth.getInstance()

        add_img.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select an image"), PICK_IMAGE_REQUEST)
        }

        upload_btn.setOnClickListener {
            val description = idescription.text.toString().trim()
            val category = icategory.text.toString().trim()
            val contact = icontact.text.toString().trim()
            val model = imodel.text.toString().trim()
            val imageID = System.currentTimeMillis().toString()
            val userID = fAuth.currentUser?.uid

            if (description.isEmpty() || category.isEmpty() || contact.isEmpty() || model.isEmpty()) {
                Toast.makeText(applicationContext, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                if (file_path.toString().isEmpty()) {
                    Toast.makeText(applicationContext, "Image Required", Toast.LENGTH_SHORT).show()
                } else {
                    val ref = storage_reference.child("Car").child(imageID)
                    progress.show()
                    ref.putFile(file_path).addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener { uri ->
                            progress.dismiss()
                            val imageUrl = uri.toString()
                            val product = Products(category, description, model, contact, imageUrl)

                            dbref.orderByKey().equalTo(userID)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    @SuppressLint("HardwareIds")
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            for (userSnapshot in snapshot.children) {
                                                val key = userSnapshot.key
                                                val user =
                                                    userSnapshot.getValue(Products::class.java)
                                                userSnapshot.child("deviceName").ref.setValue(Build.MODEL)
                                                userSnapshot.child("deviceId").ref.setValue(Settings.Secure.getString(
                                                    contentResolver,
                                                    Settings.Secure.ANDROID_ID
                                                )).addOnCompleteListener {
                                                    Toast.makeText(
                                                        this@PostCarActivity,
                                                        "User Registered Successfully!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                    .addOnFailureListener { err ->
                                                        Toast.makeText(
                                                            this@PostCarActivity,
                                                            "Error ${err.message}",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                            }
                                        } else {
                                            Toast.makeText(
                                                this@PostCarActivity,
                                                "Sorry, your password is incorrect!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(
                                            this@PostCarActivity,
                                            "An error occurred while accessing the database.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })

                            dbref.setValue(product)
                            Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        progress.dismiss()
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            file_path = data.data!!
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, file_path)
                add_img.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
