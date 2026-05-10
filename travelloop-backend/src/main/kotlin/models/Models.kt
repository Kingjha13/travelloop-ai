package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime


object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 128)
    val email = varchar("email", 256).uniqueIndex()
    val passwordHash = varchar("password_hash", 256)
    val photoUrl = varchar("photo_url", 512).nullable()
    val language = varchar("language", 16).default("en")
    val createdAt = datetime("created_at")
    override val primaryKey = PrimaryKey(id)
}

object TripsTable : Table("trips") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(UsersTable.id)
    val name = varchar("name", 256)
    val description = text("description").nullable()
    val coverPhotoUrl = varchar("cover_photo_url", 512).nullable()
    val startDate = varchar("start_date", 16)
    val endDate = varchar("end_date", 16)
    val isPublic = bool("is_public").default(false)
    val totalBudget = double("total_budget").default(0.0)
    val createdAt = datetime("created_at")
    override val primaryKey = PrimaryKey(id)
}

object StopsTable : Table("stops") {
    val id = integer("id").autoIncrement()
    val tripId = integer("trip_id").references(TripsTable.id)
    val cityName = varchar("city_name", 128)
    val country = varchar("country", 128)
    val arrivalDate = varchar("arrival_date", 16)
    val departureDate = varchar("departure_date", 16)
    val orderIndex = integer("order_index").default(0)
    val costIndex = double("cost_index").default(0.0)
    override val primaryKey = PrimaryKey(id)
}

object ActivitiesTable : Table("activities") {
    val id = integer("id").autoIncrement()
    val stopId = integer("stop_id").references(StopsTable.id)
    val name = varchar("name", 256)
    val description = text("description").nullable()
    val type = varchar("type", 64)           // sightseeing | food | adventure | transport | stay
    val cost = double("cost").default(0.0)
    val duration = integer("duration_minutes").default(60)
    val scheduledTime = varchar("scheduled_time", 32).nullable()
    val imageUrl = varchar("image_url", 512).nullable()
    override val primaryKey = PrimaryKey(id)
}

object PackingItemsTable : Table("packing_items") {
    val id = integer("id").autoIncrement()
    val tripId = integer("trip_id").references(TripsTable.id)
    val name = varchar("name", 256)
    val category = varchar("category", 64).default("general") // clothing | documents | electronics | general
    val isPacked = bool("is_packed").default(false)
    override val primaryKey = PrimaryKey(id)
}

object TripNotesTable : Table("trip_notes") {
    val id = integer("id").autoIncrement()
    val tripId = integer("trip_id").references(TripsTable.id)
    val stopId = integer("stop_id").references(StopsTable.id).nullable()
    val content = text("content")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
    override val primaryKey = PrimaryKey(id)
}

object BudgetItemsTable : Table("budget_items") {
    val id = integer("id").autoIncrement()
    val tripId = integer("trip_id").references(TripsTable.id)
    val category = varchar("category", 64) // transport | stay | activities | meals | other
    val label = varchar("label", 256)
    val amount = double("amount")
    override val primaryKey = PrimaryKey(id)
}


@Serializable
data class RegisterRequest(val name: String, val email: String, val password: String)

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class AuthResponse(val token: String, val user: UserDto)

@Serializable
data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val photoUrl: String? = null,
    val language: String = "en"
)

@Serializable
data class UpdateProfileRequest(
    val name: String? = null,
    val photoUrl: String? = null,
    val language: String? = null
)

@Serializable
data class TripRequest(
    val name: String,
    val description: String? = null,
    val coverPhotoUrl: String? = null,
    val startDate: String,
    val endDate: String,
    val isPublic: Boolean = false,
    val totalBudget: Double = 0.0
)

@Serializable
data class TripDto(
    val id: Int,
    val userId: Int,
    val name: String,
    val description: String? = null,
    val coverPhotoUrl: String? = null,
    val startDate: String,
    val endDate: String,
    val isPublic: Boolean,
    val totalBudget: Double,
    val stopCount: Int = 0,
    val stops: List<StopDto> = emptyList()
)

@Serializable
data class StopRequest(
    val cityName: String,
    val country: String,
    val arrivalDate: String,
    val departureDate: String,
    val orderIndex: Int = 0,
    val costIndex: Double = 0.0
)

@Serializable
data class StopDto(
    val id: Int,
    val tripId: Int,
    val cityName: String,
    val country: String,
    val arrivalDate: String,
    val departureDate: String,
    val orderIndex: Int,
    val costIndex: Double,
    val activities: List<ActivityDto> = emptyList()
)

@Serializable
data class ActivityRequest(
    val name: String,
    val description: String? = null,
    val type: String,
    val cost: Double = 0.0,
    val duration: Int = 60,
    val scheduledTime: String? = null,
    val imageUrl: String? = null
)

@Serializable
data class ActivityDto(
    val id: Int,
    val stopId: Int,
    val name: String,
    val description: String? = null,
    val type: String,
    val cost: Double,
    val duration: Int,
    val scheduledTime: String? = null,
    val imageUrl: String? = null
)

@Serializable
data class PackingItemRequest(val name: String, val category: String = "general")

@Serializable
data class PackingItemDto(
    val id: Int,
    val tripId: Int,
    val name: String,
    val category: String,
    val isPacked: Boolean
)

@Serializable
data class TripNoteRequest(
    val content: String,
    val stopId: Int? = null
)

@Serializable
data class TripNoteDto(
    val id: Int,
    val tripId: Int,
    val stopId: Int? = null,
    val content: String,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class BudgetItemRequest(
    val category: String,
    val label: String,
    val amount: Double
)

@Serializable
data class BudgetItemDto(
    val id: Int,
    val tripId: Int,
    val category: String,
    val label: String,
    val amount: Double
)

@Serializable
data class BudgetSummaryDto(
    val totalBudget: Double,
    val totalSpent: Double,
    val remaining: Double,
    val byCategory: Map<String, Double>,
    val items: List<BudgetItemDto>
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
)

@Serializable
data class ErrorResponse(val success: Boolean = false, val message: String)