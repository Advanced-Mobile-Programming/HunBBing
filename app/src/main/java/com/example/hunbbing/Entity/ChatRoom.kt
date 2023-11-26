package com.example.hunbbing.Entity

data class ChatRoom(
    var chatId: String = "", // 채팅내역 ID
    var chatUserName : String = "", // 채팅 상대방의 이름
    var lastChat : String = "",
    val chatRoomId : String = "",
    var receiverUid : String = ""
)
