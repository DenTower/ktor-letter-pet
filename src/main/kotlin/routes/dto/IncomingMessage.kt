package com.example.routes.dto

import kotlinx.serialization.Serializable

@Serializable
data class IncomingMessage(
    val id: String,
    val text: String,
    val chatId: String,
    val username: String,
    val timestamp: Long
)