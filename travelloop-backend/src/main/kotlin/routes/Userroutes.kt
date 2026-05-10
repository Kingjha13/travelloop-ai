package com.example.routes

import com.example.models.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.userRoutes() {

    route("/api/users") {

        get("/me") {

            val userId =
                call.principal<JWTPrincipal>()
                    ?.payload
                    ?.getClaim("userId")
                    ?.asInt()

            if (userId == null) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse(message = "Invalid token")
                )
                return@get
            }

            val row = transaction {
                UsersTable
                    .selectAll()
                    .where { UsersTable.id eq userId }
                    .firstOrNull()
            }

            if (row == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse(message = "User not found")
                )
                return@get
            }

            call.respond(
                UserDto(
                    id = row[UsersTable.id],
                    name = row[UsersTable.name],
                    email = row[UsersTable.email],
                    photoUrl = row[UsersTable.photoUrl],
                    language = row[UsersTable.language]
                )
            )
        }

        put("/me") {

            val userId =
                call.principal<JWTPrincipal>()
                    ?.payload
                    ?.getClaim("userId")
                    ?.asInt()

            if (userId == null) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse(message = "Invalid token")
                )
                return@put
            }

            val req = call.receive<UpdateProfileRequest>()

            transaction {

                UsersTable.update(
                    { UsersTable.id eq userId }
                ) {

                    req.name?.let { value ->
                        it[name] = value
                    }

                    req.photoUrl?.let { value ->
                        it[photoUrl] = value
                    }

                    req.language?.let { value ->
                        it[language] = value
                    }
                }
            }

            val row = transaction {

                UsersTable
                    .selectAll()
                    .where { UsersTable.id eq userId }
                    .first()
            }

            call.respond(
                UserDto(
                    id = row[UsersTable.id],
                    name = row[UsersTable.name],
                    email = row[UsersTable.email],
                    photoUrl = row[UsersTable.photoUrl],
                    language = row[UsersTable.language]
                )
            )
        }

        delete("/me") {

            val userId =
                call.principal<JWTPrincipal>()
                    ?.payload
                    ?.getClaim("userId")
                    ?.asInt()

            if (userId == null) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse(message = "Invalid token")
                )
                return@delete
            }

            transaction {
                UsersTable.deleteWhere {
                    UsersTable.id eq userId
                }
            }

            call.respond(HttpStatusCode.NoContent)
        }
    }
}