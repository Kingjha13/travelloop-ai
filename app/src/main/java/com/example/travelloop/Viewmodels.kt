package com.example.traveloop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelloop.TraveloopRepository
import com.example.travelloop.models.ActivityRequest
import com.example.travelloop.models.BudgetItemRequest
import com.example.travelloop.models.BudgetSummaryDto
import com.example.travelloop.models.PackingItemDto
import com.example.travelloop.models.PackingItemRequest
import com.example.travelloop.models.StopDto
import com.example.travelloop.models.StopRequest
import com.example.travelloop.models.TripDto
import com.example.travelloop.models.TripNoteDto
import com.example.travelloop.models.TripNoteRequest
import com.example.travelloop.models.TripRequest
import com.example.travelloop.models.UpdateProfileRequest
import com.example.travelloop.models.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel()

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: UserDto) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel : ViewModel() {
    private val repo = TraveloopRepository()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val res = repo.login(email, password)
                if (res.isSuccessful) {
                    val body = res.body()!!
                    RetrofitClient.saveToken(body.token)
                    _uiState.value = AuthUiState.Success(body.user)
                } else {
                    _uiState.value = AuthUiState.Error("Invalid credentials")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Network error")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val res = repo.register(name, email, password)
                if (res.isSuccessful) {
                    val body = res.body()!!
                    RetrofitClient.saveToken(body.token)
                    _uiState.value = AuthUiState.Success(body.user)
                } else {
                    _uiState.value = AuthUiState.Error("Registration failed \u2014 email may already exist")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Network error")
            }
        }
    }

    fun reset() { _uiState.value = AuthUiState.Idle }
}

class TripViewModel : ViewModel() {
    private val repo = TraveloopRepository()

    private val _trips = MutableStateFlow<List<TripDto>>(emptyList())
    val trips: StateFlow<List<TripDto>> = _trips

    private val _currentTrip = MutableStateFlow<TripDto?>(null)
    val currentTrip: StateFlow<TripDto?> = _currentTrip

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadTrips() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val res = repo.getTrips()
                if (res.isSuccessful) _trips.value = res.body() ?: emptyList()
                else _error.value = "Failed to load trips"
            } catch (e: Exception) {
                _error.value = e.message
            } finally { _loading.value = false }
        }
    }

    fun loadTrip(tripId: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val res = repo.getTrip(tripId)
                if (res.isSuccessful) _currentTrip.value = res.body()
                else _error.value = "Trip not found"
            } catch (e: Exception) {
                _error.value = e.message
            } finally { _loading.value = false }
        }
    }

    fun createTrip(req: TripRequest, onSuccess: (Int) -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val res = repo.createTrip(req)
                if (res.isSuccessful) {
                    val trip = res.body()!!
                    loadTrips()
                    onSuccess(trip.id)
                } else _error.value = "Failed to create trip"
            } catch (e: Exception) {
                _error.value = e.message
            } finally { _loading.value = false }
        }
    }

    fun updateTrip(tripId: Int, req: TripRequest, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val res = repo.updateTrip(tripId, req)
                if (res.isSuccessful) {
                    _currentTrip.value = res.body()
                    loadTrips()
                    onSuccess()
                } else _error.value = "Failed to update trip"
            } catch (e: Exception) {
                _error.value = e.message
            } finally { _loading.value = false }
        }
    }

    fun deleteTrip(tripId: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val res = repo.deleteTrip(tripId)
                if (res.isSuccessful) { loadTrips(); onSuccess() }
                else _error.value = "Failed to delete trip"
            } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun clearError() { _error.value = null }
}

class StopViewModel : ViewModel() {
    private val repo = TraveloopRepository()

    private val _stops = MutableStateFlow<List<StopDto>>(emptyList())
    val stops: StateFlow<List<StopDto>> = _stops

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadStops(tripId: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val res = repo.getStops(tripId)
                if (res.isSuccessful) _stops.value = res.body() ?: emptyList()
                else _error.value = "Failed to load stops"
            } catch (e: Exception) {
                _error.value = e.message
            } finally { _loading.value = false }
        }
    }

    fun addStop(tripId: Int, req: StopRequest, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val res = repo.createStop(tripId, req)
                if (res.isSuccessful) { loadStops(tripId); onSuccess() }
                else _error.value = "Failed to add stop"
            } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun deleteStop(tripId: Int, stopId: Int) {
        viewModelScope.launch {
            try {
                val res = repo.deleteStop(tripId, stopId)
                if (res.isSuccessful) loadStops(tripId)
                else _error.value = "Failed to delete stop"
            } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun addActivity(stopId: Int, req: ActivityRequest, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val res = repo.createActivity(stopId, req)
                if (res.isSuccessful) {
                    val tripId = _stops.value.firstOrNull()?.tripId
                    if (tripId != null) loadStops(tripId)
                    onSuccess()
                } else _error.value = "Failed to add activity"
            } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun deleteActivity(stopId: Int, activityId: Int) {
        viewModelScope.launch {
            try {
                val res = repo.deleteActivity(stopId, activityId)
                if (res.isSuccessful) {
                    val tripId = _stops.value.firstOrNull()?.tripId
                    if (tripId != null) loadStops(tripId)
                } else _error.value = "Failed to delete activity"
            } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun clearError() { _error.value = null }
}

class PackingViewModel : ViewModel() {
    private val repo = TraveloopRepository()

    private val _items = MutableStateFlow<List<PackingItemDto>>(emptyList())
    val items: StateFlow<List<PackingItemDto>> = _items

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun load(tripId: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val res = repo.getPackingItems(tripId)
                if (res.isSuccessful) _items.value = res.body() ?: emptyList()
                else _error.value = "Failed to load packing list"
            } catch (e: Exception) { _error.value = e.message }
            finally { _loading.value = false }
        }
    }

    fun add(tripId: Int, name: String, category: String?) {
        viewModelScope.launch {
            try {
                val res = repo.addPackingItem(tripId, PackingItemRequest(name, category))
                if (res.isSuccessful) load(tripId)
                else _error.value = "Failed to add item"
            } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun toggle(tripId: Int, itemId: Int) {
        viewModelScope.launch {
            try {
                val res = repo.togglePackingItem(tripId, itemId)
                if (res.isSuccessful) {
                    val newState = res.body()?.get("isPacked") ?: false
                    _items.value = _items.value.map {
                        if (it.id == itemId) it.copy(isPacked = newState) else it
                    }
                }
            } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun delete(tripId: Int, itemId: Int) {
        viewModelScope.launch {
            try {
                val res = repo.deletePackingItem(tripId, itemId)
                if (res.isSuccessful) _items.value = _items.value.filter { it.id != itemId }
                else _error.value = "Failed to delete item"
            } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun reset(tripId: Int) {
        viewModelScope.launch {
            try {
                repo.resetPackingList(tripId)
                _items.value = _items.value.map { it.copy(isPacked = false) }
            } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun clearError() { _error.value = null }
}

class NotesViewModel : ViewModel() {
    private val repo = TraveloopRepository()

    private val _notes = MutableStateFlow<List<TripNoteDto>>(emptyList())
    val notes: StateFlow<List<TripNoteDto>> = _notes

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun load(tripId: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val res = repo.getNotes(tripId)
                if (res.isSuccessful) _notes.value = res.body() ?: emptyList()
                else _error.value = "Failed to load notes"
            } catch (e: Exception) { _error.value = e.message }
            finally { _loading.value = false }
        }
    }

    fun create(tripId: Int, content: String, stopId: Int? = null) {
        viewModelScope.launch {
            try {
                val res = repo.createNote(tripId, TripNoteRequest(content, stopId))
                if (res.isSuccessful) load(tripId)
                else _error.value = "Failed to save note"
            } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun update(tripId: Int, noteId: Int, content: String) {
        viewModelScope.launch {
            try {
                val res = repo.updateNote(tripId, noteId, TripNoteRequest(content))
                if (res.isSuccessful) load(tripId)
                else _error.value = "Failed to update note"
            } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun delete(tripId: Int, noteId: Int) {
        viewModelScope.launch {
            try {
                val res = repo.deleteNote(tripId, noteId)
                if (res.isSuccessful) _notes.value = _notes.value.filter { it.id != noteId }
                else _error.value = "Failed to delete note"
            } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun clearError() { _error.value = null }
}

class BudgetViewModel : ViewModel() {
    private val repo = TraveloopRepository()

    private val _summary = MutableStateFlow<BudgetSummaryDto?>(null)
    val summary: StateFlow<BudgetSummaryDto?> = _summary

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun load(tripId: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val res = repo.getBudget(tripId)
                if (res.isSuccessful) _summary.value = res.body()
                else _error.value = "Failed to load budget"
            } catch (e: Exception) { _error.value = e.message }
            finally { _loading.value = false }
        }
    }

    fun addItem(tripId: Int, category: String, label: String, amount: Double) {
        viewModelScope.launch {
            try {
                val res = repo.addBudgetItem(tripId, BudgetItemRequest(category, label, amount))
                if (res.isSuccessful) load(tripId)
                else _error.value = "Failed to add budget item"
            } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun deleteItem(tripId: Int, itemId: Int) {
        viewModelScope.launch {
            try {
                val res = repo.deleteBudgetItem(tripId, itemId)
                if (res.isSuccessful) load(tripId)
                else _error.value = "Failed to delete budget item"
            } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun clearError() { _error.value = null }
}
class ProfileViewModel : ViewModel() {
    private val repo = TraveloopRepository()

    private val _user = MutableStateFlow<UserDto?>(null)
    val user: StateFlow<UserDto?> = _user

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun load() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val res = repo.getMe()
                if (res.isSuccessful) _user.value = res.body()
                else _error.value = "Failed to load profile"
            } catch (e: Exception) { _error.value = e.message }
            finally { _loading.value = false }
        }
    }

    fun updateProfile(name: String?, photoUrl: String?, language: String?, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val res = repo.updateProfile(UpdateProfileRequest(name, photoUrl, language))
                if (res.isSuccessful) { _user.value = res.body(); onSuccess() }
                else _error.value = "Failed to update profile"
            } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val res = repo.deleteAccount()
                if (res.isSuccessful) { RetrofitClient.clearToken(); onSuccess() }
                else _error.value = "Failed to delete account"
            } catch (e: Exception) { _error.value = e.message }
        }
    }

    fun logout() { RetrofitClient.clearToken() }

    fun clearError() { _error.value = null }
}
class PublicTripViewModel : ViewModel() {
    private val repo = TraveloopRepository()

    private val _trips = MutableStateFlow<List<TripDto>>(emptyList())
    val trips: StateFlow<List<TripDto>> = _trips

    private val _trip = MutableStateFlow<TripDto?>(null)
    val trip: StateFlow<TripDto?> = _trip

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadAll() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val res = repo.getPublicTrips()
                if (res.isSuccessful) _trips.value = res.body() ?: emptyList()
            } catch (_: Exception) { }
            finally { _loading.value = false }
        }
    }

    fun loadOne(id: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val res = repo.getPublicTrip(id)
                if (res.isSuccessful) _trip.value = res.body()
            } catch (_: Exception) { }
            finally { _loading.value = false }
        }
    }
}