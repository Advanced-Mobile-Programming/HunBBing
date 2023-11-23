package com.example.hunbbing.Entity

data class ChatRoom(
    val chatRoomId: String, // 채팅방 ID
    var chatUserName : String, // 채팅 상대방의 이름
    val lastChat : String
)
