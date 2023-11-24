package com.example.hunbbing

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddItemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_add)
    }


    val item = findViewById<EditText>(R.id.itemText)
    val price = findViewById<EditText>(R.id.priceText)
    val explain = findViewById<EditText>(R.id.explainText)
    val tag = findViewById<EditText>(R.id.tagText)

    val addImgbtn = findViewById<Button>(R.id.addImgBtn)
    val plusbtn = findViewById<Button>(R.id.plusbtn)



}