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

fun Route.stopRoutes() {

    route("/api/trips/{tripId}/stops") {

        get {

            val userId = call.principal<JWTPrincipal>()
                ?.payload?.getClaim("userId")?.asInt()

            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse(message = "Invalid token"))
                return@get
            }

            val tripId = call.parameters["tripId"]!!.toInt()

            val trip = transaction {
                TripsTable.selectAll()
                    .where { (TripsTable.id eq tripId) and (TripsTable.userId eq userId) }
                    .firstOrNull()
            }

            if (trip == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse(message = "Trip not found"))
                return@get
            }

            val stops = transaction {
                StopsTable.selectAll()
                    .where { StopsTable.tripId eq tripId }
                    .orderBy(StopsTable.orderIndex)
                    .map { row ->
                        val stopId = row[StopsTable.id]

                        val activities = ActivitiesTable.selectAll()
                            .where { ActivitiesTable.stopId eq stopId }
                            .map { a ->
                                ActivityDto(
                                    id = a[ActivitiesTable.id],
                                    stopId = stopId,
                                    name = a[ActivitiesTable.name],
                                    description = a[ActivitiesTable.description],
                                    type = a[ActivitiesTable.type],
                                    cost = a[ActivitiesTable.cost],
                                    duration = a[ActivitiesTable.duration],
                                    scheduledTime = a[ActivitiesTable.scheduledTime],
                                    imageUrl = a[ActivitiesTable.imageUrl]
                                )
                            }

                        StopDto(
                            id = stopId,
                            tripId = tripId,
                            cityName = row[StopsTable.cityName],
                            country = row[StopsTable.country],
                            arrivalDate = row[StopsTable.arrivalDate],
                            departureDate = row[StopsTable.departureDate],
                            orderIndex = row[StopsTable.orderIndex],
                            costIndex = row[StopsTable.costIndex],
                            activities = activities
                        )
                    }
            }

            call.respond(stops)
        }

        post {
            val userId = call.principal<JWTPrincipal>()
                ?.payload?.getClaim("userId")?.asInt()

            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse(message = "Invalid token"))
                return@post
            }

            val tripId = call.parameters["tripId"]!!.toInt()

            val trip = transaction {
                TripsTable.selectAll()
                    .where { (TripsTable.id eq tripId) and (TripsTable.userId eq userId) }
                    .firstOrNull()
            }

            if (trip == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse(message = "Trip not found"))
                return@post
            }

            val req = call.receive<StopRequest>()

            val stopId = transaction {
                StopsTable.insert {
                    it[StopsTable.tripId] = tripId
                    it[cityName] = req.cityName
                    it[country] = req.country
                    it[arrivalDate] = req.arrivalDate
                    it[departureDate] = req.departureDate
                    it[orderIndex] = req.orderIndex
                    it[costIndex] = req.costIndex
                }[StopsTable.id]
            }

            call.respond(
                HttpStatusCode.Created,
                StopDto(
                    id = stopId,
                    tripId = tripId,
                    cityName = req.cityName,
                    country = req.country,
                    arrivalDate = req.arrivalDate,
                    departureDate = req.departureDate,
                    orderIndex = req.orderIndex,
                    costIndex = req.costIndex
                )
            )
        }

        route("/{stopId}") {

            put {
                val userId = call.principal<JWTPrincipal>()
                    ?.payload?.getClaim("userId")?.asInt()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse(message = "Invalid token"))
                    return@put
                }

                val tripId = call.parameters["tripId"]!!.toInt()
                val stopId = call.parameters["stopId"]!!.toInt()

                val trip = transaction {
                    TripsTable.selectAll()
                        .where { (TripsTable.id eq tripId) and (TripsTable.userId eq userId) }
                        .firstOrNull()
                }

                if (trip == null) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse(message = "Trip not found"))
                    return@put
                }

                val req = call.receive<StopRequest>()

                transaction {
                    StopsTable.update({ (StopsTable.id eq stopId) and (StopsTable.tripId eq tripId) }) {
                        it[cityName] = req.cityName
                        it[country] = req.country
                        it[arrivalDate] = req.arrivalDate
                        it[departureDate] = req.departureDate
                        it[orderIndex] = req.orderIndex
                        it[costIndex] = req.costIndex
                    }
                }

                val row = transaction {
                    StopsTable.selectAll().where { StopsTable.id eq stopId }.first()
                }

                call.respond(
                    StopDto(
                        id = row[StopsTable.id],
                        tripId = tripId,
                        cityName = row[StopsTable.cityName],
                        country = row[StopsTable.country],
                        arrivalDate = row[StopsTable.arrivalDate],
                        departureDate = row[StopsTable.departureDate],
                        orderIndex = row[StopsTable.orderIndex],
                        costIndex = row[StopsTable.costIndex]
                    )
                )
            }

            delete {
                val userId = call.principal<JWTPrincipal>()
                    ?.payload?.getClaim("userId")?.asInt()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse(message = "Invalid token"))
                    return@delete
                }

                val tripId = call.parameters["tripId"]!!.toInt()
                val stopId = call.parameters["stopId"]!!.toInt()

                val trip = transaction {
                    TripsTable.selectAll()
                        .where { (TripsTable.id eq tripId) and (TripsTable.userId eq userId) }
                        .firstOrNull()
                }

                if (trip == null) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse(message = "Trip not found"))
                    return@delete
                }

                transaction {
                    StopsTable.deleteWhere { (StopsTable.id eq stopId) and (StopsTable.tripId eq tripId) }
                }

                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}