package com.example.hunbbing.Entity

data class ChatRoom(
    val chatId: String, // 채팅내역 ID
    var chatUserName : String, // 채팅 상대방의 이름
    val lastChat : String
)
