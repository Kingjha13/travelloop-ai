package com.example.traveloop

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.travelloop.models.TripRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTripScreen(
    onTripCreated: (Int) -> Unit,
    onBack: () -> Unit,
    vm: TripViewModel = viewModel()
) {
    val loading by vm.loading.collectAsState()
    val error   by vm.error.collectAsState()

    var name        by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate   by remember { mutableStateOf("") }
    var endDate     by remember { mutableStateOf("") }
    var budget      by remember { mutableStateOf("") }
    var isPublic    by remember { mutableStateOf(false) }
    var nameError   by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TraveloopTopBar(title = "New Trip", onBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            error?.let { ErrorMessage(it) }

            TraveloopTextField(
                value = name,
                onValueChange = { name = it; nameError = "" },
                label = "Trip Name *",
                leadingIcon = Icons.Default.Luggage,
                isError = nameError.isNotBlank(),
                errorText = nameError
            )

            TraveloopTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description (optional)",
                leadingIcon = Icons.Default.Notes,
                singleLine = false
            )

            var showStartPicker by remember { mutableStateOf(false) }
            var showEndPicker by remember { mutableStateOf(false) }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Box(
                    modifier = Modifier.weight(1f)
                ) {

                    OutlinedTextField(
                        value = startDate,
                        onValueChange = {},
                        readOnly = true,

                        label = {
                            Text("Start Date")
                        },

                        leadingIcon = {
                            Icon(Icons.Default.CalendarToday, null)
                        },

                        modifier = Modifier.fillMaxWidth()
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                showStartPicker = true
                            }
                    )
                }

                Box(
                    modifier = Modifier.weight(1f)
                ) {

                    OutlinedTextField(
                        value = endDate,
                        onValueChange = {},
                        readOnly = true,

                        label = {
                            Text("End Date")
                        },

                        leadingIcon = {
                            Icon(Icons.Default.CalendarToday, null)
                        },

                        modifier = Modifier.fillMaxWidth()
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                showEndPicker = true
                            }
                    )
                }
            }

            if (showStartPicker) {

                val datePickerState = rememberDatePickerState()

                DatePickerDialog(

                    onDismissRequest = {
                        showStartPicker = false
                    },

                    confirmButton = {

                        TextButton(

                            onClick = {

                                datePickerState.selectedDateMillis?.let {

                                    startDate =
                                        java.text.SimpleDateFormat(
                                            "yyyy-MM-dd",
                                            java.util.Locale.getDefault()
                                        ).format(
                                            java.util.Date(it)
                                        )
                                }

                                showStartPicker = false
                            }
                        ) {

                            Text("OK")
                        }
                    }
                ) {

                    DatePicker(
                        state = datePickerState
                    )
                }
            }

            if (showEndPicker) {

                val datePickerState = rememberDatePickerState()

                DatePickerDialog(

                    onDismissRequest = {
                        showEndPicker = false
                    },

                    confirmButton = {

                        TextButton(

                            onClick = {

                                datePickerState.selectedDateMillis?.let {

                                    endDate =
                                        java.text.SimpleDateFormat(
                                            "yyyy-MM-dd",
                                            java.util.Locale.getDefault()
                                        ).format(
                                            java.util.Date(it)
                                        )
                                }

                                showEndPicker = false
                            }
                        ) {

                            Text("OK")
                        }
                    }
                ) {

                    DatePicker(
                        state = datePickerState
                    )
                }
            }

            TraveloopTextField(
                value = budget,
                onValueChange = { budget = it },
                label = "Total Budget (optional)",
                leadingIcon = Icons.Default.AttachMoney
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Public, contentDescription = null, tint = Teal600, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Make Public", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text("Others can discover your trip", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Switch(
                    checked = isPublic,
                    onCheckedChange = { isPublic = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Teal600,
                        checkedTrackColor = Teal600.copy(alpha = 0.3f)
                    )
                )
            }

            Spacer(Modifier.height(8.dp))

            TraveloopButton(
                text = if (loading) "Creating…" else "Create Trip",
                onClick = {
                    if (name.isBlank()) {
                        nameError = "Trip name is required"
                    } else {
                        vm.createTrip(
                            TripRequest(
                                name = name.trim(),
                                description = description.takeIf { it.isNotBlank() },
                                startDate = startDate.takeIf { it.isNotBlank() },
                                endDate = endDate.takeIf { it.isNotBlank() },
                                totalBudget = budget.toDoubleOrNull() ?: 0.0,
                                isPublic = isPublic
                            ),
                            onSuccess = { id -> onTripCreated(id) }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading,
                icon = Icons.Default.Add
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    tripId: Int,
    onNavigateToBuilder: () -> Unit,
    onNavigateToBudget: () -> Unit,
    onNavigateToPacking: () -> Unit,
    onNavigateToNotes: () -> Unit,
    onBack: () -> Unit,
    vm: TripViewModel = viewModel()
) {
    val trip    by vm.currentTrip.collectAsState()
    val loading by vm.loading.collectAsState()
    val error   by vm.error.collectAsState()

    LaunchedEffect(tripId) { vm.loadTrip(tripId) }

    Scaffold(
        topBar = { TraveloopTopBar(title = trip?.name ?: "Trip", onBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (loading && trip == null) {
            LoadingContent()
            return@Scaffold
        }

        error?.let {
            ErrorMessage(it)
            return@Scaffold
        }

        trip?.let { t ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Teal600, Amber500.copy(alpha = 0.6f), Slate800)
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column {
                        Text(t.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = White)
                        if (!t.description.isNullOrBlank()) {
                            Text(t.description, style = MaterialTheme.typography.bodyMedium, color = White.copy(alpha = 0.75f))
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SurfaceChip("${t.stopCount} Stops")
                            if (t.startDate != null) SurfaceChip("${t.startDate} → ${t.endDate ?: "?"}", color = Teal600)
                            if (t.isPublic) SurfaceChip("Public", color = SuccessGreen)
                        }
                    }
                }

                if (t.totalBudget > 0) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AttachMoney, contentDescription = null, tint = Amber400, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Budget", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$${t.totalBudget}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text("  Quick Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ActionTile(icon = Icons.Default.Map, label = "Itinerary", color = Teal600, onClick = onNavigateToBuilder, modifier = Modifier.weight(1f))
                    ActionTile(icon = Icons.Default.AccountBalanceWallet, label = "Budget", color = Amber400, onClick = onNavigateToBudget, modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ActionTile(icon = Icons.Default.CheckBox, label = "Packing", color = Color(0xFF8B5CF6), onClick = onNavigateToPacking, modifier = Modifier.weight(1f))
                    ActionTile(icon = Icons.Default.Notes, label = "Notes", color = Color(0xFFEC4899), onClick = onNavigateToNotes, modifier = Modifier.weight(1f))
                }

                if ((t.stops ?: emptyList()).isNotEmpty()){
                    Spacer(Modifier.height(20.dp))
                    SectionHeader(title = "Stops", action = "Edit Itinerary", onAction = onNavigateToBuilder)

                    (t.stops ?: emptyList()).forEachIndexed { idx, stop ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Teal600.copy(alpha = 0.2f), RoundedCornerShape(50)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${idx + 1}", style = MaterialTheme.typography.labelMedium, color = Teal600, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(stop.cityName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text(stop.country, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (stop.activities.isNotEmpty()) {
                                SurfaceChip("${stop.activities.size} activities", color = Amber400)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ActionTile(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = color, fontWeight = FontWeight.SemiBold)
        }
    }
}