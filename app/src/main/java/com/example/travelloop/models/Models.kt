package com.example.travelloop.models


data class RegisterRequest(val name: String, val email: String, val password: String)

data class LoginRequest(val email: String, val password: String)

data class AuthResponse(val token: String, val user: UserDto)


data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val photoUrl: String? = null,
    val language: String? = null
)


data class UpdateProfileRequest(
    val name: String? = null,
    val photoUrl: String? = null,
    val language: String? = null
)


data class TripDto(
    val id: Int,
    val userId: Int,
    val name: String,
    val description: String? = null,
    val coverPhotoUrl: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val isPublic: Boolean = false,
    val totalBudget: Double = 0.0,
    val stopCount: Int = 0,
//    val stops: List<StopDto> = emptyList()
    val stops: List<StopDto>? = emptyList()
)

data class TripRequest(
    val name: String,
    val description: String? = null,
    val coverPhotoUrl: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val isPublic: Boolean = false,
    val totalBudget: Double = 0.0
)


data class StopDto(
    val id: Int,
    val tripId: Int,
    val cityName: String,
    val country: String,
    val arrivalDate: String? = null,
    val departureDate: String? = null,
    val orderIndex: Int = 0,
    val costIndex: Double = 0.0,
    val activities: List<ActivityDto> = emptyList()
)

data class StopRequest(
    val cityName: String,
    val country: String,
    val arrivalDate: String? = null,
    val departureDate: String? = null,
    val orderIndex: Int = 0,
    val costIndex: Double = 0.0
)


data class ActivityDto(
    val id: Int,
    val stopId: Int,
    val name: String,
    val description: String? = null,
    val type: String? = null,
    val cost: Double = 0.0,
    val duration: Int? = null,
    val scheduledTime: String? = null,
    val imageUrl: String? = null
)

data class ActivityRequest(
    val name: String,
    val description: String? = null,
    val type: String? = null,
    val cost: Double = 0.0,
    val duration: Int? = null,
    val scheduledTime: String? = null,
    val imageUrl: String? = null
)


data class PackingItemDto(
    val id: Int,
    val tripId: Int,
    val name: String,
    val category: String? = null,
    val isPacked: Boolean = false
)

data class PackingItemRequest(val name: String, val category: String? = null)


data class TripNoteDto(
    val id: Int,
    val tripId: Int,
    val stopId: Int? = null,
    val content: String,
    val createdAt: String,
    val updatedAt: String
)

data class TripNoteRequest(val content: String, val stopId: Int? = null)


data class BudgetItemDto(
    val id: Int,
    val tripId: Int,
    val category: String,
    val label: String,
    val amount: Double
)

data class BudgetItemRequest(val category: String, val label: String, val amount: Double)

data class BudgetSummaryDto(
    val totalBudget: Double,
    val totalSpent: Double,
    val remaining: Double,
    val byCategory: Map<String, Double>,
    val items: List<BudgetItemDto>
)


data class ErrorResponse(val message: String)