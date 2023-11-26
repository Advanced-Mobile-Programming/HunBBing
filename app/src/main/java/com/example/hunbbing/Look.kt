package com.example.hunbbing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
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
        val ownerName = intent.getStringExtra("owner") ?: "알 수 없음"
        owner.setText(ownerName)
        val ownerUid = intent.getStringExtra("ownerUid");
//        message.setText(intent.getStringExtra("message"))
//        like.setText(intent.getStringExtra("like"))
//        state.setText(intent.getStringExtra("state"))

        val backButton = findViewById<ImageView>(R.id.back_btn)
        backButton.setOnClickListener {
            finish()
        }


        val itemId = intent.getStringExtra("itemId") ?: ""
        val itemOwnerId = intent.getStringExtra("ownerUid") ?: ""
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        database = FirebaseDatabase.getInstance().reference

        Log.i("Look", itemOwnerId)
        Log.i("Look", itemId)
        Log.i("Look", uid)
        Log.i("Look", database.child("user").child(uid).child("chatRooms").toString())

        val editItemBtn= findViewById<Button>(R.id.editItemBtn)
        editItemBtn.setOnClickListener {
            if (uid == itemOwnerId) {
                val intent = Intent(this, EItemActivity::class.java)
                intent.putExtra("itemId", itemId)
                startActivity(intent)
            } else {
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
                    database.child("user").child(uid).child("chatRooms").child(itemOwnerId).child("chatId").setValue(chatRoomId.toString())
                    database.child("user").child(itemOwnerId).child("chatRooms").child(uid).child("chatId").setValue(chatRoomId.toString())
                    database.child("chatRooms").child(chatRoomId).child("lastChat").setValue("아직 채팅이 없습니다.")
                    val chatId = database.child("chatRooms").child(chatRoomId).child("chatId").push().key
                    database.child("chatRooms").child(chatRoomId).child("chatId").setValue(chatId)
                    Log.i("Look", "채팅방 생성 성공")
                }
                // 넘길 데이터
                var chatRoomId : String? = null
                var chatId : String? = null

                val chatRoomRef : DatabaseReference = database.child("user").child(uid).child("chatRooms").child(itemOwnerId)
                Log.i("ref", chatRoomRef.toString())
                chatRoomRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        Log.i("aa", dataSnapshot.value.toString())
                        Log.i("aa", dataSnapshot.childrenCount.toString())
                        for (childSnapshot in dataSnapshot.children) {
                            Log.i("aa", childSnapshot.value.toString())
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
        Log.i("Look", databaseReference.toString())
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