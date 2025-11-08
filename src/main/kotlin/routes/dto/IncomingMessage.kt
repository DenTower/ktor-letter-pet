package com.example.routes.dto

import kotlinx.serialization.Serializable

@Serializable
data class IncomingMessage(
    val text: String,
    val chatId: String
)