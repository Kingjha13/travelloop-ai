package com.example.traveloop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.travelloop.models.ActivityDto
import com.example.travelloop.models.ActivityRequest
import com.example.travelloop.models.StopDto
import com.example.travelloop.models.StopRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryBuilderScreen(
    tripId: Int,
    onBack: () -> Unit,
    vm: StopViewModel = viewModel()
) {
    val stops by vm.stops.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    var showAddStop by remember { mutableStateOf(false) }
    var showAddActivity by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(tripId) { vm.loadStops(tripId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Itinerary") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddStop = true }, containerColor = Teal600) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                stops.isEmpty() -> Text("No Stops Added", modifier = Modifier.align(Alignment.Center))
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(stops) { index, stop ->
                            StopCard(
                                index = index + 1,
                                stop = stop,
                                onAddActivity = { showAddActivity = stop.id },
                                onDeleteStop = { vm.deleteStop(tripId, stop.id) },
                                onDeleteActivity = { actId -> vm.deleteActivity(stop.id, actId) }
                            )
                        }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }

            error?.let {
                Text(
                    text = it,
                    color = ErrorRed,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                )
            }
        }
    }

    if (showAddStop) {
        AddStopSheet(
            onDismiss = { showAddStop = false },
            onConfirm = { city, country, arrival, departure ->
                vm.addStop(
                    tripId,
                    StopRequest(
                        cityName = city,
                        country = country,
                        arrivalDate = if (arrival.isBlank()) null else arrival,
                        departureDate = if (departure.isBlank()) null else departure,
                        orderIndex = stops.size,
                        costIndex = 0.0
                    )
                ) { showAddStop = false }
            }
        )
    }

    showAddActivity?.let { stopId ->
        AddActivitySheet(
            onDismiss = { showAddActivity = null },
            onConfirm = { name, type, cost, duration ->
                vm.addActivity(
                    stopId,
                    ActivityRequest(
                        name = name,
                        type = if (type.isBlank()) null else type,
                        cost = cost.toDoubleOrNull() ?: 0.0,
                        duration = duration.toIntOrNull()
                    )
                ) { showAddActivity = null }
            }
        )
    }
}

@Composable
private fun StopCard(
    index: Int,
    stop: StopDto,
    onAddActivity: () -> Unit,
    onDeleteStop: () -> Unit,
    onDeleteActivity: (Int) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(36.dp).background(Teal600, RoundedCornerShape(50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("$index", color = White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(stop.cityName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(stop.country, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Row {
                    IconButton(onClick = onAddActivity) { Icon(Icons.Default.AddCircle, null) }
                    IconButton(onClick = onDeleteStop) { Icon(Icons.Default.Delete, null) }
                }
            }

            if (stop.arrivalDate != null || stop.departureDate != null) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    stop.arrivalDate?.let {
                        // Using the SurfaceChip from Components.kt which has label param
                        SurfaceChip(label = "\u2708 $it", color = Amber400)
                    }
                    stop.departureDate?.let {
                        SurfaceChip(label = "\u2192 $it", color = Teal600)
                    }
                }
            }

            if (stop.activities.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                stop.activities.forEach { activity ->
                    ActivityRow(activity = activity, onDelete = { onDeleteActivity(activity.id) })
                    Spacer(Modifier.height(6.dp))
                }
            }

            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = onAddActivity, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Add Activity")
            }
        }
    }
}

@Composable
private fun ActivityRow(activity: ActivityDto, onDelete: () -> Unit) {
    val typeColor = when (activity.type?.lowercase()) {
        "food" -> Color(0xFFEF4444)
        "culture" -> Color(0xFF8B5CF6)
        "adventure" -> Color(0xFF22C55E)
        else -> Teal600
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(typeColor.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(activity.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                activity.type?.let { Text(it, color = typeColor, style = MaterialTheme.typography.labelSmall) }
                if (activity.cost > 0) Text("$${activity.cost}", color = Amber400, style = MaterialTheme.typography.labelSmall)
                activity.duration?.let { Text("${it}min", style = MaterialTheme.typography.labelSmall) }
            }
        }
        IconButton(onClick = onDelete) { Icon(Icons.Default.Close, null) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddStopSheet(onDismiss: () -> Unit, onConfirm: (String, String, String, String) -> Unit) {
    var city by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var arrival by remember { mutableStateOf("") }
    var departure by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Add Stop", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = country, onValueChange = { country = it }, label = { Text("Country") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = arrival, onValueChange = { arrival = it }, label = { Text("Arrival Date") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = departure, onValueChange = { departure = it }, label = { Text("Departure Date") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(24.dp))
            Button(onClick = { onConfirm(city, country, arrival, departure) }, modifier = Modifier.fillMaxWidth()) {
                Text("Add Stop")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddActivitySheet(onDismiss: () -> Unit, onConfirm: (String, String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Add Activity", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Activity Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("Cost") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (mins)") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(24.dp))
            Button(onClick = { onConfirm(name, type, cost, duration) }, modifier = Modifier.fillMaxWidth()) {
                Text("Add Activity")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}