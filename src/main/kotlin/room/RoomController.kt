package com.example.room

import com.example.data.ChatDataSource
import com.example.data.MessageDataSource
import com.example.data.model.Chat
import com.example.data.model.Message
import com.example.room.exceptions.ChatNotFoundException
import com.example.room.exceptions.SessionAlreadyExistsException
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val messageDataSource: MessageDataSource,
    private val chatDataSource: ChatDataSource
) {
    private val members = ConcurrentHashMap<String, Member>()

    fun onConnect(
        username: String,
        sessionId: String,
        socket: WebSocketSession
    ) {
        if (members.containsKey(username)) {
            throw SessionAlreadyExistsException()
        }
        members[username] = Member(
            username = username,
            sessionId = sessionId,
            socket = socket
        )
    }

    suspend fun sendMessage(senderUsername: String, message: String, chatId: String) {
        val messageEntity = Message(
            chatId = chatId,
            text = message,
            username = senderUsername,
            timestamp = System.currentTimeMillis(),
        )
        messageDataSource.insertMessage(messageEntity)
        members.values.forEach { member ->
            val parsedMessage = Json.encodeToString<ServerEvent>(
                ServerEvent.NewMessage(messageEntity)
            )

            member.socket.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun getAllMessagesForUser(username: String): List<Message> {
        return messageDataSource.getAllMessagesForUser(username = username)
    }

    suspend fun tryDisconnect(username: String) {
        members[username]?.socket?.close()
        if (members.containsKey(username)) {
            members.remove(username)
        }
    }

    suspend fun getAllChatsForUser(username: String): List<Chat> {
        return chatDataSource.getAllChatsForUser(username = username)
    }

    suspend fun createChat(chat: Chat): Chat {
        return chatDataSource.insertChat(chat)
    }

    suspend fun deleteChat(chatId: String) {
        chatDataSource.deleteChat(chatId)
    }

    suspend fun getAllChatMembers(chatId: String): List<String> {
        return chatDataSource.getAllChatMembers(chatId)
    }

    suspend fun addMember(username: String, chatId: String) {
        val chat = chatDataSource.insertMember(username, chatId)
            ?: throw ChatNotFoundException()

        notifyAddedToChat(username = username, chat = chat)
    }

    private suspend fun notifyAddedToChat(username: String, chat: Chat) {
        val parsedChat = Json.encodeToString(
            ServerEvent.NewChat(chat)
        )

        val member = members[username]
        member?.socket?.send(
            Frame.Text(parsedChat)
        )
    }

    suspend fun removeMemberFromChat(username: String, chatId: String) {
        chatDataSource.deleteMember(username, chatId)
    }
}