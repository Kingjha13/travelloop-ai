package com.example.routes

import com.example.models.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.activityRoutes() {

    route("/api/stops/{stopId}/activities") {

        get {

            val stopId = call.parameters["stopId"]!!.toInt()

            val activities = transaction {

                ActivitiesTable
                    .selectAll()
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
            }

            call.respond(activities)
        }

        post {

            val stopId = call.parameters["stopId"]!!.toInt()

            val req = call.receive<ActivityRequest>()

            val actId = transaction {

                ActivitiesTable.insert {

                    it[ActivitiesTable.stopId] = stopId

                    it[name] = req.name

                    it[description] = req.description

                    it[type] = req.type

                    it[cost] = req.cost

                    it[duration] = req.duration

                    it[scheduledTime] = req.scheduledTime

                    it[imageUrl] = req.imageUrl

                }[ActivitiesTable.id]
            }

            call.respond(
                HttpStatusCode.Created,

                ActivityDto(
                    id = actId,
                    stopId = stopId,
                    name = req.name,
                    description = req.description,
                    type = req.type,
                    cost = req.cost,
                    duration = req.duration,
                    scheduledTime = req.scheduledTime,
                    imageUrl = req.imageUrl
                )
            )
        }

        route("/{activityId}") {

            put {

                val stopId = call.parameters["stopId"]!!.toInt()

                val activityId =
                    call.parameters["activityId"]!!.toInt()

                val req = call.receive<ActivityRequest>()

                transaction {

                    ActivitiesTable.update(
                        {
                            (ActivitiesTable.id eq activityId) and
                                    (ActivitiesTable.stopId eq stopId)
                        }
                    ) {

                        it[name] = req.name

                        it[description] = req.description

                        it[type] = req.type

                        it[cost] = req.cost

                        it[duration] = req.duration

                        it[scheduledTime] = req.scheduledTime

                        it[imageUrl] = req.imageUrl
                    }
                }

                val a = transaction {

                    ActivitiesTable
                        .selectAll()
                        .where {
                            ActivitiesTable.id eq activityId
                        }
                        .first()
                }

                call.respond(

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
                )
            }

            delete {

                val stopId = call.parameters["stopId"]!!.toInt()

                val activityId =
                    call.parameters["activityId"]!!.toInt()

                transaction {

                    ActivitiesTable.deleteWhere {

                        (ActivitiesTable.id eq activityId) and
                                (ActivitiesTable.stopId eq stopId)
                    }
                }

                call.respond(HttpStatusCode.NoContent)
            }
        }
    }

    get("/api/activities/search") {

        val query =
            call.request.queryParameters["q"] ?: ""

        val type =
            call.request.queryParameters["type"]

        val activities = transaction {

            val baseQuery =
                ActivitiesTable.selectAll()

            val filteredQuery =
                if (type != null) {
                    baseQuery.andWhere {
                        ActivitiesTable.type eq type
                    }
                } else {
                    baseQuery
                }

            filteredQuery.map { a ->

                ActivityDto(
                    id = a[ActivitiesTable.id],
                    stopId = a[ActivitiesTable.stopId],
                    name = a[ActivitiesTable.name],
                    description = a[ActivitiesTable.description],
                    type = a[ActivitiesTable.type],
                    cost = a[ActivitiesTable.cost],
                    duration = a[ActivitiesTable.duration],
                    scheduledTime = a[ActivitiesTable.scheduledTime],
                    imageUrl = a[ActivitiesTable.imageUrl]
                )
            }.filter {

                it.name.contains(
                    query,
                    ignoreCase = true
                )
            }
        }

        call.respond(activities)
    }
}