package com.example.carbid

import android.widget.EditText

class CarPoster {
    var cartegory:String = ""
    var description:String = ""
    var model:String = ""
    var contact:String = ""
    var userID:String = ""
    var carID:String =""
    var imagedes:String = ""

    constructor(
        cartegory: EditText,
        description: EditText,
        model: EditText,
        contact: EditText,
        userID: String,
        carID: String,
        imagedes: String
    ) {
        this.cartegory = cartegory.toString()
        this.model = model.toString()
        this.contact = contact.toString()
        this.description = description.toString()
        this.userID = userID
        this.carID = carID
        this.imagedes = imagedes
    }
    constructor()
}