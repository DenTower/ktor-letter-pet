package com.example

import com.example.room.RoomController
import com.example.routes.addMemberToChat
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import com.example.routes.chatSocket
import com.example.routes.createChat
import com.example.routes.deleteChat
import com.example.routes.removeMemberFromChat
import com.example.routes.getAllChatMembers
import com.example.routes.getAllChatsForUser
import com.example.routes.getAllMessagesForUser
import com.example.routes.syncMessages


fun Application.configureRouting() {
    val roomController by inject<RoomController>()
    install(RoutingRoot) {
        chatSocket(roomController)

        getAllMessagesForUser(roomController)

        getAllChatsForUser(roomController)
        createChat(roomController)
        deleteChat(roomController)

        getAllChatMembers(roomController)
        addMemberToChat(roomController)
        removeMemberFromChat(roomController)

        syncMessages(roomController)
    }
}
