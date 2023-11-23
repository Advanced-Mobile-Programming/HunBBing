package com.example.hunbbing.Entity

data class ChatMessage(
    val senderId: String?, // 발신자 UID
    val message: String?, // 내용
    val timestamp: Long?
){
    constructor():this("", "", 0)
}
