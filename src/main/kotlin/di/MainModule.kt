package com.example.di

import com.example.data.MessageDataSource
import com.example.data.MessageDataSourceImpl
import com.example.room.RoomController
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mongoUri = System.getenv("MONGO_URI") ?: "mongodb://localhost:27017"

val mainModule = module {
    single {
        KMongo.createClient(mongoUri)
            .coroutine
            .getDatabase("letter_pet_db")
    }
    single<MessageDataSource> {
        MessageDataSourceImpl(get())
    }
    single {
        RoomController(get())
    }
}