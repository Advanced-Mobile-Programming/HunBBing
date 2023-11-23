package com.example.hunbbing

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hunbbing.Entity.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class MessageAdapter(private val context: Context, private val messageList: ArrayList<ChatMessage>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val receive = 1 // 보내기
    private val send = 2 // 받기
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == 1){
            ReceiveViewHolder(LayoutInflater.from(context).inflate(R.layout.item_receive_message, parent, false))
        }
        else SendViewHolder(LayoutInflater.from(context).inflate(R.layout.item_send_message, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val curMessage = messageList[position]

        if(holder.javaClass == SendViewHolder::class.java){
            val viewHolder = holder as SendViewHolder
            viewHolder.sendMessage.text = curMessage.message
        }
        else {
            val viewHolder = holder as ReceiveViewHolder
            viewHolder.receiveMessage.text = curMessage.message
        }
    }
    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(FirebaseAuth.getInstance().currentUser?.uid.equals(messageList[position].senderId)){
            send
        }
        else receive
    }

    class SendViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val sendMessage: TextView = itemView.findViewById(R.id.send_message_text)
    }

    class ReceiveViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val receiveMessage: TextView = itemView.findViewById(R.id.receive_message_text)
    }
}