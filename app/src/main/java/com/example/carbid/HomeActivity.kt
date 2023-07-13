package com.example.carbid

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity() {

    lateinit var listProducts: GridView
    lateinit var products: ArrayList<Products>
    lateinit var adapter: Adapter
    lateinit var progress: ProgressDialog
    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        listProducts=findViewById(R.id.gridView)
        products= ArrayList()
        adapter= Adapter(this, products)
        progress= ProgressDialog(this)
        progress.setTitle("Loading")
        progress.setMessage("Please wait...")
        var ref = FirebaseDatabase.getInstance().reference.child("Products")
        progress.show()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                products.clear()
                for (snap in snapshot.children){
                    var product = snap.getValue(Products::class.java) as? Products
                    product?.let { products.add(it) }

                }
                adapter.notifyDataSetChanged()
                progress.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "DB inaccessible", Toast.LENGTH_SHORT).show()
            }
        })
        listProducts.adapter= adapter
        listProducts.setOnItemClickListener { _, _, position, _ ->
            var intent = Intent(this, ViewCarActivity::class.java)
            intent.putExtra("Category", products[position].category)
            intent.putExtra("Contact", products[position].contact)
            intent.putExtra("Description", products[position].description)
            intent.putExtra("Model", products[position].model)
            intent.putExtra("Image", products[position].productImage)
            startActivity(intent)
        }
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_home

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_post -> {
                    val intent = Intent(this, PostCarActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_cart -> {
                    val intent = Intent(this, CollectionActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }


    }
}