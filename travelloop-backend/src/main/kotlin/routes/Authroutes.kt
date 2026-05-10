package com.example.routes

import com.example.models.*
import com.example.plugins.generateToken
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDateTime

fun Route.authRoutes() {

    route("/api/auth") {

        post("/register") {

            val req = call.receive<RegisterRequest>()

            if (
                req.name.isBlank() ||
                req.email.isBlank() ||
                req.password.isBlank()
            ) {

                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        message = "Name, email and password are required"
                    )
                )

                return@post
            }

            val existing = transaction {

                UsersTable
                    .selectAll()
                    .where {
                        UsersTable.email eq req.email
                    }
                    .firstOrNull()
            }

            if (existing != null) {

                call.respond(
                    HttpStatusCode.Conflict,
                    ErrorResponse(
                        message = "Email already registered"
                    )
                )

                return@post
            }

            val hash =
                BCrypt.hashpw(
                    req.password,
                    BCrypt.gensalt()
                )

            val userId = transaction {

                UsersTable.insert {

                    it[name] = req.name

                    it[email] = req.email

                    it[passwordHash] = hash

                    it[createdAt] = LocalDateTime.now()

                }[UsersTable.id]
            }

            val user = UserDto(
                id = userId,
                name = req.name,
                email = req.email
            )

            val token =
                generateToken(
                    call.application.environment.config,
                    userId,
                    req.email
                )

            call.respond(
                HttpStatusCode.Created,
                AuthResponse(
                    token = token,
                    user = user
                )
            )
        }

        post("/login") {

            val req =
                call.receive<LoginRequest>()

            val row = transaction {

                UsersTable
                    .selectAll()
                    .where {
                        UsersTable.email eq req.email
                    }
                    .firstOrNull()
            }

            if (row == null) {

                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse(
                        message = "Invalid credentials"
                    )
                )

                return@post
            }

            val validPassword =
                BCrypt.checkpw(
                    req.password,
                    row[UsersTable.passwordHash]
                )

            if (!validPassword) {

                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse(
                        message = "Invalid credentials"
                    )
                )

                return@post
            }

            val user = UserDto(

                id = row[UsersTable.id],

                name = row[UsersTable.name],

                email = row[UsersTable.email],

                photoUrl = row[UsersTable.photoUrl],

                language = row[UsersTable.language]
            )

            val token =
                generateToken(
                    call.application.environment.config,
                    user.id,
                    user.email
                )

            call.respond(
                AuthResponse(
                    token = token,
                    user = user
                )
            )
        }
    }
}