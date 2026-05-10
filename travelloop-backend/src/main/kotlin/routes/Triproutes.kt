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
import java.time.LocalDateTime


private fun buildTripDto(tripRow: ResultRow, withStops: Boolean = false): TripDto {

    val tripId = tripRow[TripsTable.id]

    return transaction {

        val stopCount = StopsTable.selectAll()
            .where { StopsTable.tripId eq tripId }
            .count()
            .toInt()

        val stops = if (withStops) {
            StopsTable.selectAll()
                .where { StopsTable.tripId eq tripId }
                .orderBy(StopsTable.orderIndex)
                .map { stopRow ->
                    val stopId = stopRow[StopsTable.id]

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
                        cityName = stopRow[StopsTable.cityName],
                        country = stopRow[StopsTable.country],
                        arrivalDate = stopRow[StopsTable.arrivalDate],
                        departureDate = stopRow[StopsTable.departureDate],
                        orderIndex = stopRow[StopsTable.orderIndex],
                        costIndex = stopRow[StopsTable.costIndex],
                        activities = activities
                    )
                }
        } else emptyList()

        TripDto(
            id = tripId,
            userId = tripRow[TripsTable.userId],
            name = tripRow[TripsTable.name],
            description = tripRow[TripsTable.description],
            coverPhotoUrl = tripRow[TripsTable.coverPhotoUrl],
            startDate = tripRow[TripsTable.startDate],
            endDate = tripRow[TripsTable.endDate],
            isPublic = tripRow[TripsTable.isPublic],
            totalBudget = tripRow[TripsTable.totalBudget],
            stopCount = stopCount,
            stops = stops
        )
    }
}

fun Route.tripRoutes() {

    route("/api/trips") {

        get {
            val userId = call.principal<JWTPrincipal>()
                ?.payload?.getClaim("userId")?.asInt()

            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse(message = "Invalid token"))
                return@get
            }

            val trips = transaction {
                TripsTable.selectAll()
                    .where { TripsTable.userId eq userId }
                    .orderBy(TripsTable.createdAt, SortOrder.DESC)
                    .map { buildTripDto(it) }
            }

            call.respond(trips)
        }

        post {
            val userId = call.principal<JWTPrincipal>()
                ?.payload?.getClaim("userId")?.asInt()

            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse(message = "Invalid token"))
                return@post
            }

            val req = call.receive<TripRequest>()

            if (req.name.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(message = "Trip name is required"))
                return@post
            }

            val tripId = transaction {
                TripsTable.insert {
                    it[TripsTable.userId] = userId
                    it[name] = req.name
                    it[description] = req.description
                    it[coverPhotoUrl] = req.coverPhotoUrl
                    it[startDate] = req.startDate
                    it[endDate] = req.endDate
                    it[isPublic] = req.isPublic
                    it[totalBudget] = req.totalBudget
                    it[createdAt] = LocalDateTime.now()
                }[TripsTable.id]
            }

            val trip = transaction {
                TripsTable.selectAll()
                    .where { TripsTable.id eq tripId }
                    .first()
                    .let { buildTripDto(it) }
            }

            call.respond(HttpStatusCode.Created, trip)
        }

        route("/{tripId}") {

            get {
                val userId = call.principal<JWTPrincipal>()
                    ?.payload?.getClaim("userId")?.asInt()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse(message = "Invalid token"))
                    return@get
                }

                val tripId = call.parameters["tripId"]!!.toInt()

                val row = transaction {
                    TripsTable.selectAll()
                        .where { (TripsTable.id eq tripId) and (TripsTable.userId eq userId) }
                        .firstOrNull()
                }

                if (row == null) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse(message = "Trip not found"))
                    return@get
                }

                call.respond(buildTripDto(row, withStops = true))
            }

            put {
                val userId = call.principal<JWTPrincipal>()
                    ?.payload?.getClaim("userId")?.asInt()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse(message = "Invalid token"))
                    return@put
                }

                val tripId = call.parameters["tripId"]!!.toInt()
                val req = call.receive<TripRequest>()

                val updated = transaction {
                    TripsTable.update({ (TripsTable.id eq tripId) and (TripsTable.userId eq userId) }) {
                        it[name] = req.name
                        it[description] = req.description
                        it[coverPhotoUrl] = req.coverPhotoUrl
                        it[startDate] = req.startDate
                        it[endDate] = req.endDate
                        it[isPublic] = req.isPublic
                        it[totalBudget] = req.totalBudget
                    }
                }

                if (updated == 0) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse(message = "Trip not found"))
                    return@put
                }

                val row = transaction {
                    TripsTable.selectAll().where { TripsTable.id eq tripId }.first()
                }

                call.respond(buildTripDto(row, withStops = true))
            }

            delete {
                val userId = call.principal<JWTPrincipal>()
                    ?.payload?.getClaim("userId")?.asInt()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse(message = "Invalid token"))
                    return@delete
                }

                val tripId = call.parameters["tripId"]!!.toInt()

                transaction {
                    TripsTable.deleteWhere { (TripsTable.id eq tripId) and (TripsTable.userId eq userId) }
                }

                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}

fun Route.publicTripRoutes() {

    get("/api/public/trips/{tripId}") {
        val tripId = call.parameters["tripId"]!!.toInt()

        val row = transaction {
            TripsTable.selectAll()
                .where { (TripsTable.id eq tripId) and (TripsTable.isPublic eq true) }
                .firstOrNull()
        }

        if (row == null) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse(message = "Trip not found or not public"))
            return@get
        }

        call.respond(buildTripDto(row, withStops = true))
    }

    get("/api/public/trips") {
        val trips = transaction {
            TripsTable.selectAll()
                .where { TripsTable.isPublic eq true }
                .orderBy(TripsTable.createdAt, SortOrder.DESC)
                .limit(50)
                .map { buildTripDto(it) }
        }

        call.respond(trips)
    }
}