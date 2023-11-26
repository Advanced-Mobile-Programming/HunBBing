package com.example.hunbbing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hunbbing.Entity.ChatMessage
import com.example.hunbbing.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {

    private lateinit var receiverName: String
    private lateinit var receiverUid: String
    private lateinit var chatId: String
    private lateinit var chatRoomId: String

    //바인딩 객체
    private lateinit var binding: ActivityChatBinding

    private lateinit var messageList: ArrayList<ChatMessage>

    lateinit var auth: FirebaseAuth
    lateinit var DBref: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messageList = ArrayList()
        val messageAdapter: MessageAdapter = MessageAdapter(this, messageList)

        // RecyclerView
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = messageAdapter

        // 넘어온 데이터 변수에 담기
        receiverName = intent.getStringExtra("name").toString()
        receiverUid = intent.getStringExtra("uId").toString()
        chatId = intent.getStringExtra("chatId").toString()
        chatRoomId = intent.getStringExtra("chatRoomId").toString()


        auth = FirebaseAuth.getInstance()
        DBref = FirebaseDatabase.getInstance().reference

        // 현재 접속자 Uid
        val senderUid = auth.currentUser?.uid

        binding.receiverNameTv.text = receiverName
        binding.ivBack.setOnClickListener {
            finish()
        }

        // 메세지 전송 버튼 이벤트
        binding.sendBtn.setOnClickListener{

            val message = binding.messageEdit.text.toString()
            val messageObject = ChatMessage(senderUid, message, System.currentTimeMillis())

            // 데이터 저장
            DBref.child("chats").child(chatId).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    // 저장 성공하면
                    Log.i("message", "메세지 전송 성공")
                    DBref.child("chatRooms").child(chatRoomId).child("lastChat").setValue(message)
                }

            //입력값 초기화
            binding.messageEdit.setText("")
        }

        // 메세지 가져오기
        DBref.child("chats").child(chatId).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()

                    for(postSnapShot in snapshot.children){
                        val message = postSnapShot.getValue(ChatMessage::class.java)
                        messageList.add(message!!)
                    }

                    messageAdapter.notifyDataSetChanged()
                    val lastIndex = messageAdapter.itemCount - 1
                    binding.chatRecyclerView.scrollToPosition(lastIndex)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }
}