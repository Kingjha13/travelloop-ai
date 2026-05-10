package com.example.routes

import com.example.models.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

fun Route.packingRoutes() {

    route("/api/trips/{tripId}/packing") {

        get {

            val tripId =
                call.parameters["tripId"]!!.toInt()

            val items = transaction {

                PackingItemsTable
                    .selectAll()
                    .where {
                        PackingItemsTable.tripId eq tripId
                    }
                    .map { row ->

                        PackingItemDto(
                            id = row[PackingItemsTable.id],
                            tripId = tripId,
                            name = row[PackingItemsTable.name],
                            category = row[PackingItemsTable.category],
                            isPacked = row[PackingItemsTable.isPacked]
                        )
                    }
            }

            call.respond(items)
        }

        post {

            val tripId =
                call.parameters["tripId"]!!.toInt()

            val req =
                call.receive<PackingItemRequest>()

            val itemId = transaction {

                PackingItemsTable.insert {

                    it[PackingItemsTable.tripId] = tripId

                    it[name] = req.name

                    it[category] = req.category

                    it[isPacked] = false

                }[PackingItemsTable.id]
            }

            call.respond(
                HttpStatusCode.Created,

                PackingItemDto(
                    id = itemId,
                    tripId = tripId,
                    name = req.name,
                    category = req.category,
                    isPacked = false
                )
            )
        }

        patch("/{itemId}/toggle") {

            val itemId =
                call.parameters["itemId"]!!.toInt()

            val current = transaction {

                PackingItemsTable
                    .selectAll()
                    .where {
                        PackingItemsTable.id eq itemId
                    }
                    .firstOrNull()
            }

            if (current == null) {

                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse(
                        message = "Item not found"
                    )
                )

                return@patch
            }

            val newState =
                !current[PackingItemsTable.isPacked]

            transaction {

                PackingItemsTable.update(
                    {
                        PackingItemsTable.id eq itemId
                    }
                ) {

                    it[isPacked] = newState
                }
            }

            call.respond(
                mapOf(
                    "isPacked" to newState
                )
            )
        }

        delete("/{itemId}") {

            val itemId =
                call.parameters["itemId"]!!.toInt()

            transaction {

                PackingItemsTable.deleteWhere {

                    PackingItemsTable.id eq itemId
                }
            }

            call.respond(HttpStatusCode.NoContent)
        }

        post("/reset") {

            val tripId =
                call.parameters["tripId"]!!.toInt()

            transaction {

                PackingItemsTable.update(
                    {
                        PackingItemsTable.tripId eq tripId
                    }
                ) {

                    it[isPacked] = false
                }
            }

            call.respond(
                mapOf(
                    "message" to "Checklist reset"
                )
            )
        }
    }
}

fun Route.noteRoutes() {

    route("/api/trips/{tripId}/notes") {

        get {

            val tripId =
                call.parameters["tripId"]!!.toInt()

            val notes = transaction {

                TripNotesTable
                    .selectAll()
                    .where {
                        TripNotesTable.tripId eq tripId
                    }
                    .orderBy(
                        TripNotesTable.createdAt,
                        SortOrder.DESC
                    )
                    .map { row ->

                        TripNoteDto(
                            id = row[TripNotesTable.id],
                            tripId = tripId,
                            stopId = row[TripNotesTable.stopId],
                            content = row[TripNotesTable.content],
                            createdAt = row[TripNotesTable.createdAt].toString(),
                            updatedAt = row[TripNotesTable.updatedAt].toString()
                        )
                    }
            }

            call.respond(notes)
        }

        post {

            val tripId =
                call.parameters["tripId"]!!.toInt()

            val req =
                call.receive<TripNoteRequest>()

            val now =
                LocalDateTime.now()

            val noteId = transaction {

                TripNotesTable.insert {

                    it[TripNotesTable.tripId] = tripId

                    it[stopId] = req.stopId

                    it[content] = req.content

                    it[createdAt] = now

                    it[updatedAt] = now

                }[TripNotesTable.id]
            }

            call.respond(
                HttpStatusCode.Created,

                TripNoteDto(
                    id = noteId,
                    tripId = tripId,
                    stopId = req.stopId,
                    content = req.content,
                    createdAt = now.toString(),
                    updatedAt = now.toString()
                )
            )
        }

        put("/{noteId}") {

            val noteId =
                call.parameters["noteId"]!!.toInt()

            val req =
                call.receive<TripNoteRequest>()

            val now =
                LocalDateTime.now()

            transaction {

                TripNotesTable.update(
                    {
                        TripNotesTable.id eq noteId
                    }
                ) {

                    it[content] = req.content

                    it[updatedAt] = now
                }
            }

            val row = transaction {

                TripNotesTable
                    .selectAll()
                    .where {
                        TripNotesTable.id eq noteId
                    }
                    .first()
            }

            call.respond(

                TripNoteDto(
                    id = row[TripNotesTable.id],
                    tripId = row[TripNotesTable.tripId],
                    stopId = row[TripNotesTable.stopId],
                    content = row[TripNotesTable.content],
                    createdAt = row[TripNotesTable.createdAt].toString(),
                    updatedAt = row[TripNotesTable.updatedAt].toString()
                )
            )
        }

        delete("/{noteId}") {

            val noteId =
                call.parameters["noteId"]!!.toInt()

            transaction {

                TripNotesTable.deleteWhere {

                    TripNotesTable.id eq noteId
                }
            }

            call.respond(HttpStatusCode.NoContent)
        }
    }
}

fun Route.budgetRoutes() {

    route("/api/trips/{tripId}/budget") {

        get {

            val tripId =
                call.parameters["tripId"]!!.toInt()

            val trip = transaction {

                TripsTable
                    .selectAll()
                    .where {
                        TripsTable.id eq tripId
                    }
                    .firstOrNull()
            }

            if (trip == null) {

                call.respond(
                    HttpStatusCode.NotFound,
                    ErrorResponse(
                        message = "Trip not found"
                    )
                )

                return@get
            }

            val items = transaction {

                BudgetItemsTable
                    .selectAll()
                    .where {
                        BudgetItemsTable.tripId eq tripId
                    }
                    .map { row ->

                        BudgetItemDto(
                            id = row[BudgetItemsTable.id],
                            tripId = tripId,
                            category = row[BudgetItemsTable.category],
                            label = row[BudgetItemsTable.label],
                            amount = row[BudgetItemsTable.amount]
                        )
                    }
            }

            val byCategory =
                items.groupBy { it.category }
                    .mapValues { (_, v) ->
                        v.sumOf { it.amount }
                    }

            val totalSpent =
                items.sumOf { it.amount }

            val totalBudget =
                trip[TripsTable.totalBudget]

            call.respond(

                BudgetSummaryDto(
                    totalBudget = totalBudget,
                    totalSpent = totalSpent,
                    remaining = totalBudget - totalSpent,
                    byCategory = byCategory,
                    items = items
                )
            )
        }

        post {

            val tripId =
                call.parameters["tripId"]!!.toInt()

            val req =
                call.receive<BudgetItemRequest>()

            val itemId = transaction {

                BudgetItemsTable.insert {

                    it[BudgetItemsTable.tripId] = tripId

                    it[category] = req.category

                    it[label] = req.label

                    it[amount] = req.amount

                }[BudgetItemsTable.id]
            }

            call.respond(
                HttpStatusCode.Created,

                BudgetItemDto(
                    id = itemId,
                    tripId = tripId,
                    category = req.category,
                    label = req.label,
                    amount = req.amount
                )
            )
        }

        delete("/{itemId}") {

            val itemId =
                call.parameters["itemId"]!!.toInt()

            transaction {

                BudgetItemsTable.deleteWhere {

                    BudgetItemsTable.id eq itemId
                }
            }

            call.respond(HttpStatusCode.NoContent)
        }
    }
}