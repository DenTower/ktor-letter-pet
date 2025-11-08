package com.example.routes.dto

import kotlinx.serialization.Serializable

@Serializable
data class AddMemberRequest(
    val username: String,
    val chatId: String,
)