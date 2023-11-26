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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await

class Look : AppCompatActivity() {
    lateinit var database: DatabaseReference
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
        val ownerName = intent.getStringExtra("owner") ?: "알 수 없음"
        owner.setText(ownerName)
        val ownerUid = intent.getStringExtra("ownerUid");
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

        val itemId = intent.getStringExtra("itemId") ?: ""
        val itemOwnerId = intent.getStringExtra("ownerUid") ?: ""
        val imageUrl = intent.getStringExtra("img") ?: ""
        Glide.with(this)
            .load(imageUrl)
            .into(imgView)
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        database = FirebaseDatabase.getInstance().reference

        val editItemBtn= findViewById<Button>(R.id.editItemBtn)
        editItemBtn.setOnClickListener {
            if (uid == itemOwnerId) {
                val intent = Intent(this, EItemActivity::class.java)
                intent.putExtra("itemId", itemId)
                intent.putExtra("product_st",pr_st)
                startActivity(intent)
            } else {
                Log.d("LookActivity", "Item ID: $itemId, Item Owner ID: $itemOwnerId, User ID: $uid")
                showError("이 아이템을 수정할 권한이 없습니다.")
            }
        }

        val chatBtn = findViewById<Button>(R.id.chatBtn)

        chatBtn.setOnClickListener {
            // 채팅방이 없는 경우 채팅방 생성
            val intent = Intent(this, ChatActivity::class.java)
            isChatRoomExists("user/$uid/chatRooms", itemOwnerId){
                result ->
                if(!result){
                    Log.i("Look", "채팅방이 존재하지 않아 채팅방을 새로 생성합니다.")
                    val chatRoomId =
                        database.child("user").child(uid).child("chatRooms").child(itemOwnerId)
                            .push().key.toString()
                    database.child("user").child(uid).child("chatRooms").child(itemOwnerId).child("chatRoomId").setValue(chatRoomId.toString())
                    database.child("user").child(itemOwnerId).child("chatRooms").child(uid).child("chatRoomId").setValue(chatRoomId.toString())
                    database.child("chatRooms").child(chatRoomId).child("lastChat").setValue("아직 채팅이 없습니다.")
                    val chatId = database.child("chatRooms").child(chatRoomId).child("chatId").push().key
                    database.child("chatRooms").child(chatRoomId).child("chatId").setValue(chatId)
                    Log.i("Look", "채팅방 생성 성공")
                }
                // 넘길 데이터
                var chatRoomId : String? = null
                var chatId : String? = null

                val chatRoomRef : DatabaseReference = database.child("user").child(uid).child("chatRooms").child(itemOwnerId)
                chatRoomRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (childSnapshot in dataSnapshot.children) {
                            chatRoomId = childSnapshot.value.toString()
                        }
                        if(chatRoomId != null) {
                            database.child("chatRooms").child(chatRoomId!!).child("chatId")
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        chatId = dataSnapshot.value.toString()
                                        intent.putExtra("name", ownerName)
                                        intent.putExtra("uId", itemOwnerId)
                                        intent.putExtra("chatId", chatId)
                                        intent.putExtra("chatRoomId", chatRoomId)

                                        // 채팅 액티비티로 전송

                                        startActivity(intent)
                                    }
                                    override fun onCancelled(databaseError: DatabaseError) {
                                        // 데이터 읽기 실패

                                    }
                                })
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        // 데이터 읽기 실패

                    }
                })
            }
        }

    }

    private fun showError(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()



    }

    // Firebase 데이터 존재 여부를 확인하는 함수
    private fun isChatRoomExists(parentPath: String, childKey: String, callback: (Boolean) -> Unit) {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("$parentPath/$childKey")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // 데이터 스냅샷이 존재하면 해당 데이터가 존재하는 것으로 간주
                callback(dataSnapshot.exists())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 오류 처리
                callback(false)
            }
        })
    }
}