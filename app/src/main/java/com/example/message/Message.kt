package com.example.message

data class Message(
    var text: String = "",
    var isSent: Boolean = false,
    var senderId: String = "",
    var receiverId: String = "",
    var timestamp: Long = 0L
) {
    constructor() : this("", false, "", "", 0L)
}
