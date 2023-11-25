package com.example.hunbbing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase

class Look : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_look)
        var imgUser = findViewById<ImageView>(R.id.imgUser)
//        var imgLike = findViewById<ImageView>(R.id.imgLike)
//        var imgMsg = findViewById<ImageView>(R.id.imgMsg)
        var imgView = findViewById<ImageView>(R.id.product)
        val name = findViewById<TextView>(R.id.product_name)
        val price = findViewById<TextView>(R.id.product_price)
        val intro = findViewById<TextView>(R.id.product_intro)
        val tag = findViewById<TextView>(R.id.product_tag)
        val owner = findViewById<TextView>(R.id.product_owner)
//        val message = findViewById<TextView>(R.id.product_message)
//        val like = findViewById<TextView>(R.id.product_like)
//        val state = findViewById<Spinner>(R.id.product_state)
        name.setText(intent.getStringExtra("name"))
        price.setText(intent.getStringExtra("price"))
        intro.setText(intent.getStringExtra("intro"))
        tag.setText(intent.getStringExtra("tag"))
        owner.setText(intent.getStringExtra("owner"))
//        message.setText(intent.getStringExtra("message"))
//        like.setText(intent.getStringExtra("like"))
//        state.setText(intent.getStringExtra("state"))

        val backButton = findViewById<ImageView>(R.id.back_btn)
        backButton.setOnClickListener {
            finish()
        }


        val editItemBtn= findViewById<Button>(R.id.editItemBtn)
        val chatBtn = findViewById<Button>(R.id.chatBtn)



    }

    private fun showError(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()



    }
}