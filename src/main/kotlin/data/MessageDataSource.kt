package com.example.data

import com.example.data.model.Message

interface MessageDataSource {

    suspend fun getAllMessagesForUser(username: String): List<Message>

    suspend fun insertMessage(message: Message)
}