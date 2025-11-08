package com.example.data

import com.example.data.model.Chat
import com.example.data.model.ChatMember
import com.example.data.model.Message
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`
import org.litote.kmongo.setValue

class MessageDataSourceImpl(
    private val db: CoroutineDatabase
): MessageDataSource {

    private val messages = db.getCollection<Message>()

    override suspend fun getAllMessagesForUser(username: String): List<Message> {
        val chatMembers = db.getCollection<ChatMember>()

        val userChatIds = chatMembers.find(ChatMember::username eq username)
            .toList()
            .map { it.chatId }

        return messages.find(Message::chatId `in` userChatIds)
            .descendingSort(Message::timestamp)
            .toList()
    }

    override suspend fun insertMessage(message: Message) {
        messages.insertOne(message)

        val chats = db.getCollection<Chat>()
        chats.updateOne(
            filter = Chat::id eq message.chatId,
            update = setValue(Chat::lastMessageId, message.id)
        )
    }
}