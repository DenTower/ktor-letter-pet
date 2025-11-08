package com.example.data.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Chat(
    val name: String,
    val isGroup: Boolean,
    val createdBy: String,
    val lastMessageId: String? = null,
    @BsonId
    val id: String = ObjectId().toString()
)
