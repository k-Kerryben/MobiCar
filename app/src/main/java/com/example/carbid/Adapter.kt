package com.example.carbid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class Adapter(private val context: Context, private val productList: ArrayList<Products>) : BaseAdapter() {

    override fun getCount(): Int {
        return productList.size
    }

    override fun getItem(position: Int): Any {
        return productList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_car_post, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val product = productList[position]

        viewHolder.cartegoryTextView.text = product.category
        viewHolder.descriptionTextView.text = product.description
        viewHolder.modelTextView.text = product.model
        viewHolder.contactTextView.text = product.contact

        // Load product image using Picasso or any other image loading library
        Picasso.get().load(product.productImage).into(viewHolder.productImageView)

        return view
    }

    private class ViewHolder(view: View) {
        val productImageView: ImageView = view.findViewById(R.id.productImageView)
        val cartegoryTextView: TextView = view.findViewById(R.id.cartegoryTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
        val modelTextView: TextView = view.findViewById(R.id.modelTextView)
        val contactTextView: TextView = view.findViewById(R.id.contactTextView)
    }
}
