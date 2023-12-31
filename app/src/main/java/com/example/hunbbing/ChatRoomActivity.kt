package com.example.hunbbing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hunbbing.Entity.ChatRoom
import com.example.hunbbing.databinding.ActivityChatRoomBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class ChatRoomEntity(
    val chatId: String = "", // 채팅내역 ID
    val lastChat : String = ""
)

class ChatRoomActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatRoomBinding
    lateinit var adapter: ChatRoomAdapter

    private lateinit var auth: FirebaseAuth
    private lateinit var DBref: DatabaseReference

    private lateinit var chatRoomList: ArrayList<ChatRoom>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        DBref = FirebaseDatabase.getInstance().reference

        chatRoomList = ArrayList()

        adapter = ChatRoomAdapter(this, chatRoomList)

        binding.chatRoomRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRoomRecyclerView.adapter = adapter


        binding.ivBack.setOnClickListener {
            finish()
        }

        val curUserUid = auth.currentUser?.uid
        var index = -1;
        // 사용자 정보 가져오기
        if (curUserUid != null) {
            DBref.child("user").child(curUserUid).child("chatRooms").addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val len = snapshot.childrenCount
                    chatRoomList.clear()
                    for(postSnapshot in snapshot.children){
                        index += 1;
                        val receiverUid = postSnapshot.key.toString()
                        DBref.child("user").child(curUserUid).child("chatRooms").child(receiverUid).child("chatRoomId").addListenerForSingleValueEvent(object:ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val chatRoomId = snapshot.value.toString()
                                DBref.child("user").child(receiverUid).child("name")
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val receiverName = snapshot.value.toString()
                                            DBref.child("chatRooms").child(chatRoomId).addValueEventListener(object:ValueEventListener{
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    val curEntity = snapshot.getValue(ChatRoomEntity::class.java)
                                                    if (curEntity != null) {
                                                        chatRoomList.add(
                                                            ChatRoom(
                                                                curEntity.chatId,
                                                                receiverName,
                                                                curEntity.lastChat,
                                                                chatRoomId,
                                                                receiverUid
                                                            )
                                                        )
                                                    }
                                                    if(index.toLong() == len - 1) adapter.notifyDataSetChanged()
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    TODO("Not yet implemented")
                                                }
                                            })
                                        }
                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }
                                    })

                            }
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
    /*private fun recursive(index : Int, len : Int){
        if(index == len) {
            adapter.notifyDataSetChanged()
            return
        }
        val receiverUid = chatRoomNameList.get(index)
        DBref.child("user").child(receiverUid).child("name")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatRoomList.add(ChatRoom("",snapshot.value.toString(),"",chatRoomIdList.get(index)))

                    DBref.child("chatRooms").child(chatRoomIdList.get(index)).addValueEventListener(object:ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for(chatRoomSnapshot in snapshot.children){
                                val curEntity = chatRoomSnapshot.getValue(ChatRoomEntity::class.java)
                                val curChatRoom = chatRoomList.get(index)
                                if (curEntity != null) {
                                    curChatRoom.chatId = curEntity.chatId
                                }
                                if (curEntity != null) {
                                    curChatRoom.lastChat = curEntity.lastChat
                                }
                            }
                            recursive(index + 1, len)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }*/
}