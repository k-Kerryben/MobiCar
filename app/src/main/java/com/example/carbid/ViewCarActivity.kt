package com.example.carbid

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class ViewCarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_car)
        // Retrieve the product details from the intent
        val Category = intent.getStringExtra("Category")
        val Model = intent.getStringExtra("Model")
        val Description = intent.getStringExtra("Description")
        val Contact = intent.getStringExtra("Contact")
        val Image = intent.getStringExtra("Image")

        // Initialize the views
        val productImageView: ImageView = findViewById(R.id.productImageView)
        val CategoryTextView: TextView = findViewById(R.id.nameTextView)
        val ContactTextView: TextView = findViewById(R.id.quantityTextView)
        val ModelTextView: TextView = findViewById(R.id.priceTextView)
        val DescriptionTextView: TextView = findViewById(R.id.descTextView)

        // Set the product details to the views
        Picasso.get().load(Image).into(productImageView)
        CategoryTextView.text = Category
        ContactTextView.text = Contact
        ModelTextView.text = Model
        DescriptionTextView.text = Description
    }
}