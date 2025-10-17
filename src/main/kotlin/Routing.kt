package com.example

import com.example.room.RoomController
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import com.example.routes.chatSocket
import com.example.routes.getAllMessages


fun Application.configureRouting() {
    val roomController by inject<RoomController>()
    install(RoutingRoot) {
        chatSocket(roomController)
        getAllMessages(roomController)
    }
}
