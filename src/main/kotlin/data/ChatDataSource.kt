package com.example.data

import com.example.data.model.Chat


interface ChatDataSource {

    suspend fun getAllChatsForUser(username: String): List<Chat>

    suspend fun insertChat(chat: Chat): Chat

    suspend fun deleteChat(chatId: String)

    suspend fun getAllChatMembers(chatId: String): List<String>

    suspend fun insertMember(username: String, chatId: String): Chat?

    suspend fun deleteMember(username: String, chatId: String)
}