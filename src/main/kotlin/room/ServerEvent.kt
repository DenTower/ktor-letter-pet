package com.example.room

import com.example.data.model.Chat
import com.example.data.model.Message
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed class ServerEvent {
    @Serializable
    @SerialName("NewMessage")
    data class NewMessage(val message: Message) : ServerEvent()
    @Serializable
    @SerialName("NewChat")
    data class NewChat(val chat: Chat) : ServerEvent()
}
