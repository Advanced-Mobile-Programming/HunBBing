package com.example.hunbbing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hunbbing.Entity.ChatRoom
import com.example.hunbbing.databinding.ActivityChatBinding
import com.example.hunbbing.databinding.ActivityChatRoomBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatRoomActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatRoomBinding
    lateinit var adapter: ChatRoomAdapter

    private lateinit var auth: FirebaseAuth
    private lateinit var DBref: DatabaseReference

    private lateinit var chatRoomList: ArrayList<ChatRoom>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        DBref = FirebaseDatabase.getInstance().reference

        chatRoomList = ArrayList()

        adapter = ChatRoomAdapter(this, chatRoomList)

        binding.chatRoomRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRoomRecyclerView.adapter = adapter


        val curUserUid = auth.currentUser?.uid

        // 사용자 정보 가져오기
        if (curUserUid != null) {
            DBref.child("user").child(curUserUid).child("chatRooms").addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(postSnapshot in snapshot.children){
                        val curChatRoom = postSnapshot.getValue(ChatRoom::class.java)

                        if(!chatRoomList.contains(curChatRoom)){
                            chatRoomList.add(curChatRoom!!)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
}