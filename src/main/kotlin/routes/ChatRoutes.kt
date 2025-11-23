package com.example.routes

import com.example.data.model.Chat
import com.example.data.model.Message
import com.example.routes.dto.IncomingMessage
import com.example.room.exceptions.SessionAlreadyExistsException
import com.example.room.RoomController
import com.example.room.exceptions.ChatNotFoundException
import com.example.routes.dto.AddMemberRequest
import com.example.session.ChatSession
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json

fun Route.chatSocket(roomController: RoomController) {
    webSocket("/chat-socket") {
        val session = call.sessions.get<ChatSession>()

        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
            return@webSocket
        }

        try {
            roomController.onConnect(
                username = session.username,
                sessionId = session.sessionId,
                socket = this
            )

            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    val messageJson = frame.readText()
                    val message = Json.decodeFromString<IncomingMessage>(messageJson)

                    roomController.sendMessage(message)
                }
            }

        } catch (e: SessionAlreadyExistsException) {
            call.respond(HttpStatusCode.Conflict)

        } catch (e: Exception) {
            e.printStackTrace()

        } finally {
            roomController.tryDisconnect(session.username)
        }
    }
}

fun Route.getAllMessagesForUser(roomController: RoomController) {
    get("/{username}/messages") {
        val username = call.parameters["username"] ?: return@get call.respond(HttpStatusCode.BadRequest)

        call.respond(
            HttpStatusCode.OK,
            roomController.getAllMessagesForUser(username)
        )
    }
}

fun Route.getAllChatsForUser(roomController: RoomController) {
    get("/{username}/chats") {
        val username = call.parameters["username"] ?: return@get call.respond(HttpStatusCode.BadRequest)

        call.respond(
            HttpStatusCode.OK,
            roomController.getAllChatsForUser(username)
        )
    }
}

fun Route.createChat(roomController: RoomController) {
    post("/new/chat") {
        val request = call.receive<Chat>()

        call.respond(
            HttpStatusCode.OK,
            roomController.createChat(request)
        )
    }
}

fun Route.deleteChat(roomController: RoomController) {
    delete("/chat/{chatId}") {
        val chatId = call.parameters["chatId"] ?: return@delete call.respond(HttpStatusCode.BadRequest)

        roomController.deleteChat(chatId)
        call.respond(HttpStatusCode.OK)
    }
}

fun Route.getAllChatMembers(roomController: RoomController) {
    get("/chat/{chatId}/members") {
        val chatId = call.parameters["chatId"] ?: return@get call.respond(HttpStatusCode.BadRequest)

        call.respond(
            HttpStatusCode.OK,
            roomController.getAllChatMembers(chatId)
        )
    }
}

fun Route.addMemberToChat(roomController: RoomController) {
    post("/new/member") {
        try {
            val request = call.receive<AddMemberRequest>()

            roomController.addMember(request.username, request.chatId)
            call.respond(HttpStatusCode.OK)
        } catch (e: ChatNotFoundException) {
            call.respond(HttpStatusCode.NotFound, e.message ?: "Chat not found")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, e.message ?: "Unexpected error")
        }
    }
}

fun Route.removeMemberFromChat(roomController: RoomController) {
    delete("/chat/{chatId}/members/{username}") {
        val username = call.parameters["username"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
        val chatId = call.parameters["chatId"] ?: return@delete call.respond(HttpStatusCode.BadRequest)

        roomController.removeMemberFromChat(username, chatId)
        call.respond(HttpStatusCode.OK)
    }
}

fun Route.syncMessages(roomController: RoomController) {
    post("/sync/messages") {
        try {
            val messages = call.receive<List<IncomingMessage>>()

            messages.forEach { message ->
                roomController.sendMessage(message)
            }
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, e.message ?: "Unexpected error")
        }
    }
}