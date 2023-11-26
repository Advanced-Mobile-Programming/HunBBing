package com.example.hunbbing

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hunbbing.Entity.ChatRoom
import com.google.firebase.database.DataSnapshot

class ChatRoomAdapter(private val context: Context, private val chatRoomList: ArrayList<ChatRoom>):
RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder>(){

    class ChatRoomViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val nameText: TextView = itemView.findViewById(R.id.chatroom_user_name)
        val lastChat: TextView = itemView.findViewById(R.id.chatroom_last_chat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val view: View = LayoutInflater.from(context).
            inflate(R.layout.item_chat_room, parent, false)

        return ChatRoomViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chatRoomList.size
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        val curChatRoom = chatRoomList[position]
        holder.nameText.text = curChatRoom.chatUserName
        holder.lastChat.text = curChatRoom.lastChat

        holder.itemView.setOnClickListener{
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", curChatRoom.chatUserName)
            intent.putExtra("chatId", curChatRoom.chatId)
            intent.putExtra("chatRoomId", curChatRoom.chatRoomId)
            intent.putExtra("uId", curChatRoom.receiverUid)

            context.startActivity(intent)
        }
    }
}
