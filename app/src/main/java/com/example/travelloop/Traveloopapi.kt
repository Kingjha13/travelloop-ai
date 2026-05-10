package com.example.traveloop

import com.example.travelloop.models.ActivityDto
import com.example.travelloop.models.ActivityRequest
import com.example.travelloop.models.AuthResponse
import com.example.travelloop.models.BudgetItemDto
import com.example.travelloop.models.BudgetItemRequest
import com.example.travelloop.models.BudgetSummaryDto
import com.example.travelloop.models.LoginRequest
import com.example.travelloop.models.PackingItemDto
import com.example.travelloop.models.PackingItemRequest
import com.example.travelloop.models.RegisterRequest
import com.example.travelloop.models.StopDto
import com.example.travelloop.models.StopRequest
import com.example.travelloop.models.TripDto
import com.example.travelloop.models.TripNoteDto
import com.example.travelloop.models.TripNoteRequest
import com.example.travelloop.models.TripRequest
import com.example.travelloop.models.UpdateProfileRequest
import com.example.travelloop.models.UserDto
import retrofit2.Response
import retrofit2.http.*

interface TraveloopApi {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("api/users/me")
    suspend fun getMe(): Response<UserDto>

    @PUT("api/users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UserDto>

    @DELETE("api/users/me")
    suspend fun deleteAccount(): Response<Unit>

    @GET("api/trips")
    suspend fun getTrips(): Response<List<TripDto>>

    @POST("api/trips")
    suspend fun createTrip(@Body request: TripRequest): Response<TripDto>

    @GET("api/trips/{tripId}")
    suspend fun getTrip(@Path("tripId") tripId: Int): Response<TripDto>

    @PUT("api/trips/{tripId}")
    suspend fun updateTrip(@Path("tripId") tripId: Int, @Body request: TripRequest): Response<TripDto>

    @DELETE("api/trips/{tripId}")
    suspend fun deleteTrip(@Path("tripId") tripId: Int): Response<Unit>

    @GET("api/public/trips")
    suspend fun getPublicTrips(): Response<List<TripDto>>

    @GET("api/public/trips/{tripId}")
    suspend fun getPublicTrip(@Path("tripId") tripId: Int): Response<TripDto>

    @GET("api/trips/{tripId}/stops")
    suspend fun getStops(@Path("tripId") tripId: Int): Response<List<StopDto>>

    @POST("api/trips/{tripId}/stops")
    suspend fun createStop(@Path("tripId") tripId: Int, @Body request: StopRequest): Response<StopDto>

    @PUT("api/trips/{tripId}/stops/{stopId}")
    suspend fun updateStop(
        @Path("tripId") tripId: Int,
        @Path("stopId") stopId: Int,
        @Body request: StopRequest
    ): Response<StopDto>

    @DELETE("api/trips/{tripId}/stops/{stopId}")
    suspend fun deleteStop(@Path("tripId") tripId: Int, @Path("stopId") stopId: Int): Response<Unit>

    @GET("api/stops/{stopId}/activities")
    suspend fun getActivities(@Path("stopId") stopId: Int): Response<List<ActivityDto>>

    @POST("api/stops/{stopId}/activities")
    suspend fun createActivity(@Path("stopId") stopId: Int, @Body request: ActivityRequest): Response<ActivityDto>

    @PUT("api/stops/{stopId}/activities/{activityId}")
    suspend fun updateActivity(
        @Path("stopId") stopId: Int,
        @Path("activityId") activityId: Int,
        @Body request: ActivityRequest
    ): Response<ActivityDto>

    @DELETE("api/stops/{stopId}/activities/{activityId}")
    suspend fun deleteActivity(
        @Path("stopId") stopId: Int,
        @Path("activityId") activityId: Int
    ): Response<Unit>

    @GET("api/activities/search")
    suspend fun searchActivities(
        @Query("q") query: String = "",
        @Query("type") type: String? = null
    ): Response<List<ActivityDto>>

    @GET("api/trips/{tripId}/packing")
    suspend fun getPackingItems(@Path("tripId") tripId: Int): Response<List<PackingItemDto>>

    @POST("api/trips/{tripId}/packing")
    suspend fun addPackingItem(@Path("tripId") tripId: Int, @Body request: PackingItemRequest): Response<PackingItemDto>

    @PATCH("api/trips/{tripId}/packing/{itemId}/toggle")
    suspend fun togglePackingItem(
        @Path("tripId") tripId: Int,
        @Path("itemId") itemId: Int
    ): Response<Map<String, Boolean>>

    @DELETE("api/trips/{tripId}/packing/{itemId}")
    suspend fun deletePackingItem(@Path("tripId") tripId: Int, @Path("itemId") itemId: Int): Response<Unit>

    @POST("api/trips/{tripId}/packing/reset")
    suspend fun resetPackingList(@Path("tripId") tripId: Int): Response<Map<String, String>>

    @GET("api/trips/{tripId}/notes")
    suspend fun getNotes(@Path("tripId") tripId: Int): Response<List<TripNoteDto>>

    @POST("api/trips/{tripId}/notes")
    suspend fun createNote(@Path("tripId") tripId: Int, @Body request: TripNoteRequest): Response<TripNoteDto>

    @PUT("api/trips/{tripId}/notes/{noteId}")
    suspend fun updateNote(
        @Path("tripId") tripId: Int,
        @Path("noteId") noteId: Int,
        @Body request: TripNoteRequest
    ): Response<TripNoteDto>

    @DELETE("api/trips/{tripId}/notes/{noteId}")
    suspend fun deleteNote(@Path("tripId") tripId: Int, @Path("noteId") noteId: Int): Response<Unit>

    @GET("api/trips/{tripId}/budget")
    suspend fun getBudget(@Path("tripId") tripId: Int): Response<BudgetSummaryDto>

    @POST("api/trips/{tripId}/budget")
    suspend fun addBudgetItem(@Path("tripId") tripId: Int, @Body request: BudgetItemRequest): Response<BudgetItemDto>

    @DELETE("api/trips/{tripId}/budget/{itemId}")
    suspend fun deleteBudgetItem(@Path("tripId") tripId: Int, @Path("itemId") itemId: Int): Response<Unit>
}