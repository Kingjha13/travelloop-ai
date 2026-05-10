package com.example.plugins

import com.example.routes.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/health") { call.respond(mapOf("status" to "ok")) }

        authRoutes()
        publicTripRoutes()

        authenticate("auth-jwt") {
            userRoutes()
            tripRoutes()
            stopRoutes()
            activityRoutes()
            packingRoutes()
            noteRoutes()
            budgetRoutes()
        }
    }
}