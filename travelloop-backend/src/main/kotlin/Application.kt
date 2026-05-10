package com.example


import com.example.database.DatabaseFactory
import com.example.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init(environment.config)

    configureSecurity()
    configureSerialization()
    configureCORS()
    configureStatusPages()
    configureRouting()
}