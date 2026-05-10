package com.example.traveloop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.collections.take

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToMyTrips: () -> Unit,
    onNavigateToCreateTrip: () -> Unit,
    onNavigateToTrip: (Int) -> Unit,
    onNavigateToPublic: () -> Unit,
    onNavigateToProfile: () -> Unit,
    tripVm: TripViewModel = viewModel(),
    profileVm: ProfileViewModel = viewModel()
) {
    val trips by tripVm.trips.collectAsState()
    val user by profileVm.user.collectAsState()
    val loading by tripVm.loading.collectAsState()

    LaunchedEffect(Unit) {
        tripVm.loadTrips()
        profileVm.load()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateTrip,
                containerColor = Amber400,
                contentColor = Slate900,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Trip")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(listOf(Slate800, MaterialTheme.colorScheme.background))
                        )
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Hello, ${user?.name ?: "Traveler"} \uD83D\uDC4B",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                            Text(
                                text = "Where to next?",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Slate200
                            )
                        }
                        IconButton(
                            onClick = onNavigateToProfile,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Teal600.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Profile", tint = Teal600)
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        label = "My Trips",
                        value = trips.size.toString(),
                        icon = Icons.Default.Luggage,
                        color = Amber400,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Stops",
                        value = trips.sumOf { it.stopCount }.toString(),
                        icon = Icons.Default.Place,
                        color = Teal600,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Public",
                        value = trips.count { it.isPublic }.toString(),
                        icon = Icons.Default.Public,
                        color = Color(0xFF6366F1),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionCard(
                        icon = Icons.Default.Luggage,
                        label = "My Trips",
                        color = Amber400,
                        onClick = onNavigateToMyTrips,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        icon = Icons.Default.Public,
                        label = "Explore",
                        color = Teal600,
                        onClick = onNavigateToPublic,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        icon = Icons.Default.Add,
                        label = "New Trip",
                        color = Color(0xFF6366F1),
                        onClick = onNavigateToCreateTrip,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                SectionHeader(
                    title = "Recent Trips",
                    action = if (trips.size > 3) "See all" else null,
                    onAction = onNavigateToMyTrips
                )
            }

            if (loading) {
                item { LoadingContent() }
            } else if (trips.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Default.Flight,
                        title = "No trips yet",
                        subtitle = "Tap + to plan your first adventure"
                    )
                }
            } else {
                items(items = trips.take(5), key = { it.id }) { trip ->
                    TripCard(
                        trip = trip,
                        onClick = { onNavigateToTrip(trip.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.SemiBold)
        }
    }
}