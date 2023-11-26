package com.example.hunbbing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase

class Look : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_look)
        var imgUser = findViewById<ImageView>(R.id.imgUser)
//        var imgLike = findViewById<ImageView>(R.id.imgLike)
//        var imgMsg = findViewById<ImageView>(R.id.imgMsg)
        var imgView = findViewById<ImageView>(R.id.product) //이게 이미지임
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
        var pr_st = true
        val state_sp = findViewById<Spinner>(R.id.product_state)
        val array_pr = resources.getStringArray(R.array.product_state)
        val adapter_sp = ArrayAdapter(this,android.R.layout.simple_list_item_1,array_pr)
        state_sp.adapter = adapter_sp
        state_sp.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when(array_pr[p2]){
                    "판매중" -> pr_st = true
                    "판매 완료" -> pr_st = false
                    else -> finish()
                }
            }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
            }

        }

        val backButton = findViewById<ImageView>(R.id.back_btn)
        backButton.setOnClickListener {
            finish()
        }

        val imageUrl = intent.getStringExtra("img") ?: ""


        Glide.with(this)
            .load(imageUrl)
            .into(imgView)

        val itemId = intent.getStringExtra("ITEM_ID") ?: ""
        val itemOwnerId = intent.getStringExtra("ITEM_OWNER_ID") ?: ""
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val editItemBtn= findViewById<Button>(R.id.editItemBtn)
        editItemBtn.setOnClickListener {
            if (uid == itemOwnerId) {
                val intent = Intent(this, SellListActivity::class.java)
                intent.putExtra("ITEM_ID", itemId)
                intent.putExtra("product_st",pr_st)
                startActivity(intent)
            } else {
                Log.d("LookActivity", "Item ID: $itemId, Item Owner ID: $itemOwnerId, User ID: $uid")
                showError("이 아이템을 수정할 권한이 없습니다.")
            }
        }


    }

    private fun showError(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()



    }
}