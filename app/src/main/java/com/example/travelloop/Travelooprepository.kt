package com.example.travelloop


import com.example.travelloop.models.ActivityRequest
import com.example.travelloop.models.BudgetItemRequest
import com.example.travelloop.models.LoginRequest
import com.example.travelloop.models.PackingItemRequest
import com.example.travelloop.models.RegisterRequest
import com.example.travelloop.models.StopRequest
import com.example.travelloop.models.TripNoteRequest
import com.example.travelloop.models.TripRequest
import com.example.travelloop.models.UpdateProfileRequest
import com.example.traveloop.RetrofitClient

class TraveloopRepository {
    private val api = RetrofitClient.api

    suspend fun register(name: String, email: String, password: String) =
        api.register(RegisterRequest(name, email, password))

    suspend fun login(email: String, password: String) =
        api.login(LoginRequest(email, password))

    suspend fun getMe() = api.getMe()
    suspend fun updateProfile(req: UpdateProfileRequest) = api.updateProfile(req)
    suspend fun deleteAccount() = api.deleteAccount()

    suspend fun getTrips() = api.getTrips()
    suspend fun createTrip(req: TripRequest) = api.createTrip(req)
    suspend fun getTrip(id: Int) = api.getTrip(id)
    suspend fun updateTrip(id: Int, req: TripRequest) = api.updateTrip(id, req)
    suspend fun deleteTrip(id: Int) = api.deleteTrip(id)

    suspend fun getPublicTrips() = api.getPublicTrips()
    suspend fun getPublicTrip(id: Int) = api.getPublicTrip(id)

    suspend fun getStops(tripId: Int) = api.getStops(tripId)
    suspend fun createStop(tripId: Int, req: StopRequest) = api.createStop(tripId, req)
    suspend fun updateStop(tripId: Int, stopId: Int, req: StopRequest) = api.updateStop(tripId, stopId, req)
    suspend fun deleteStop(tripId: Int, stopId: Int) = api.deleteStop(tripId, stopId)

    suspend fun getActivities(stopId: Int) = api.getActivities(stopId)
    suspend fun createActivity(stopId: Int, req: ActivityRequest) = api.createActivity(stopId, req)
    suspend fun updateActivity(stopId: Int, actId: Int, req: ActivityRequest) = api.updateActivity(stopId, actId, req)
    suspend fun deleteActivity(stopId: Int, actId: Int) = api.deleteActivity(stopId, actId)
    suspend fun searchActivities(query: String, type: String? = null) = api.searchActivities(query, type)

    suspend fun getPackingItems(tripId: Int) = api.getPackingItems(tripId)
    suspend fun addPackingItem(tripId: Int, req: PackingItemRequest) = api.addPackingItem(tripId, req)
    suspend fun togglePackingItem(tripId: Int, itemId: Int) = api.togglePackingItem(tripId, itemId)
    suspend fun deletePackingItem(tripId: Int, itemId: Int) = api.deletePackingItem(tripId, itemId)
    suspend fun resetPackingList(tripId: Int) = api.resetPackingList(tripId)

    suspend fun getNotes(tripId: Int) = api.getNotes(tripId)
    suspend fun createNote(tripId: Int, req: TripNoteRequest) = api.createNote(tripId, req)
    suspend fun updateNote(tripId: Int, noteId: Int, req: TripNoteRequest) = api.updateNote(tripId, noteId, req)
    suspend fun deleteNote(tripId: Int, noteId: Int) = api.deleteNote(tripId, noteId)

    suspend fun getBudget(tripId: Int) = api.getBudget(tripId)
    suspend fun addBudgetItem(tripId: Int, req: BudgetItemRequest) = api.addBudgetItem(tripId, req)
    suspend fun deleteBudgetItem(tripId: Int, itemId: Int) = api.deleteBudgetItem(tripId, itemId)
}