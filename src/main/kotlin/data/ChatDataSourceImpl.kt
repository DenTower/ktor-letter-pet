package com.example.data

import com.example.data.model.Chat
import com.example.data.model.ChatMember
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`

class ChatDataSourceImpl(
    private val db: CoroutineDatabase
): ChatDataSource {

    private val chats = db.getCollection<Chat>()

    val chatMembers = db.getCollection<ChatMember>()

    override suspend fun getAllChatsForUser(username: String): List<Chat> {
        val userChatIds = chatMembers.find(ChatMember::username eq username)
            .toList()
            .map { it.chatId }
        return chats.find(Chat::id `in` userChatIds)
            .descendingSort(Chat::lastMessageId)
            .toList()
    }

    override suspend fun insertChat(chat: Chat): Chat {
        chats.insertOne(chat)

        chatMembers.insertOne(ChatMember(chat.createdBy, chat.id))

        return chat
    }

    override suspend fun deleteChat(chatId: String) {
        chats.deleteOne(Chat::id eq chatId)

        chatMembers.deleteMany(ChatMember::chatId eq chatId)
    }

    override suspend fun getAllChatMembers(chatId: String): List<String> {
        return chatMembers.find(ChatMember::chatId eq chatId)
            .descendingSort(ChatMember::username)
            .toList()
            .map { it.username }
    }

    override suspend fun insertMember(username: String, chatId: String): Chat? {
        val chat = chats.findOne(Chat::id eq chatId) ?: return null

        val alreadyExists = chatMembers.findOne(
            ChatMember::chatId eq chatId,
            ChatMember::username eq username
        ) != null

        if (!alreadyExists) {
            chatMembers.insertOne(ChatMember(username, chatId))
        }

        return chat
    }

    override suspend fun deleteMember(username: String, chatId: String) {
        chatMembers.deleteOne(ChatMember::username eq username, ChatMember::chatId eq chatId)
    }

}