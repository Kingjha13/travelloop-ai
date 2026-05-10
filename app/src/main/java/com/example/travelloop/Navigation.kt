package com.example.travelloop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.traveloop.BudgetScreen
import com.example.traveloop.CreateTripScreen
import com.example.traveloop.DashboardScreen
import com.example.traveloop.ItineraryBuilderScreen
import com.example.traveloop.LoginScreen
import com.example.traveloop.NotesScreen
import com.example.traveloop.PackingListScreen
import com.example.traveloop.RegisterScreen
import com.example.traveloop.SharedViewModel
import com.example.traveloop.TripDetailScreen
import com.example.traveloop.TripViewModel

@OptIn(ExperimentalMaterial3Api::class)

sealed class Screen(val route: String) {

    object Login : Screen("login")

    object Register : Screen("register")

    object Dashboard : Screen("dashboard")

    object MyTrips : Screen("my_trips")

    object CreateTrip : Screen("create_trip")

    object TripDetail : Screen("trip_detail/{tripId}") {
        fun createRoute(tripId: Int) =
            "trip_detail/$tripId"
    }

    object ItineraryBuilder :
        Screen("itinerary_builder/{tripId}") {

        fun createRoute(tripId: Int) =
            "itinerary_builder/$tripId"
    }

    object Budget :
        Screen("budget/{tripId}") {

        fun createRoute(tripId: Int) =
            "budget/$tripId"
    }

    object PackingList :
        Screen("packing/{tripId}") {

        fun createRoute(tripId: Int) =
            "packing/$tripId"
    }

    object Notes :
        Screen("notes/{tripId}") {

        fun createRoute(tripId: Int) =
            "notes/$tripId"
    }

    object Profile : Screen("profile")

    object PublicTrips : Screen("public_trips")

    object PublicTripDetail :
        Screen("public_trip_detail/{tripId}") {

        fun createRoute(tripId: Int) =
            "public_trip_detail/$tripId"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTripsScreen(
    onNavigateToTrip: (Int) -> Unit,
    onNavigateToCreate: () -> Unit,
    onBack: () -> Unit,
    vm: TripViewModel = viewModel()
) {

    val trips = vm.trips.collectAsState().value

    val loading = vm.loading.collectAsState().value

    LaunchedEffect(Unit) {
        vm.loadTrips()
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("My Trips")
                }
            )
        },

        floatingActionButton = {

            FloatingActionButton(
                onClick = onNavigateToCreate
            ) {

                Icon(
                    Icons.Default.Add,
                    contentDescription = null
                )
            }
        }

    ) { padding ->

        if (loading) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),

                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator()
            }

        } else if (trips.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),

                contentAlignment = Alignment.Center
            ) {

                Text("No trips found")
            }

        } else {

            LazyColumn(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),

                contentPadding = PaddingValues(16.dp),

                verticalArrangement = Arrangement.spacedBy(12.dp)

            ) {

                items(trips) { trip ->

                    Card(

                        onClick = {
                            onNavigateToTrip(trip.id)
                        },

                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(16.dp)

                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = trip.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = trip.description ?: "No description",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                Text(
                                    text = "${trip.stopCount} Stops",
                                    style = MaterialTheme.typography.labelMedium
                                )

                                Text(
                                    text = "$${trip.totalBudget}",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(80.dp))
                }
            }
        }
    }
}
@Composable
fun AppNavigation(
    startDestination: String = Screen.Login.route
) {

    val navController = rememberNavController()

    val sharedVm: SharedViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Screen.Login.route) {

            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {

            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Dashboard.route) {

            DashboardScreen(
                onNavigateToMyTrips = {
                    navController.navigate(Screen.MyTrips.route)
                },

                onNavigateToCreateTrip = {
                    navController.navigate(Screen.CreateTrip.route)
                },

                onNavigateToTrip = { id: Int ->
                    navController.navigate(
                        Screen.TripDetail.createRoute(id)
                    )
                },

                onNavigateToPublic = {
                    navController.navigate(Screen.PublicTrips.route)
                },

                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        composable(Screen.MyTrips.route) {

            MyTripsScreen(
                onNavigateToTrip = { id: Int ->
                    navController.navigate(
                        Screen.TripDetail.createRoute(id)
                    )
                },

                onNavigateToCreate = {
                    navController.navigate(Screen.CreateTrip.route)
                },

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.CreateTrip.route) {

            CreateTripScreen(
                onTripCreated = { id: Int ->

                    navController.navigate(
                        Screen.TripDetail.createRoute(id)
                    ) {
                        popUpTo(Screen.CreateTrip.route) {
                            inclusive = true
                        }
                    }
                },

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.TripDetail.route,
            arguments = listOf(
                navArgument("tripId") {
                    type = NavType.IntType
                }
            )
        ) { backStack ->

            val tripId =
                backStack.arguments?.getInt("tripId") ?: 0

            TripDetailScreen(
                tripId = tripId,

                onNavigateToBuilder = {
                    navController.navigate(
                        Screen.ItineraryBuilder.createRoute(tripId)
                    )
                },

                onNavigateToBudget = {
                    navController.navigate(
                        Screen.Budget.createRoute(tripId)
                    )
                },

                onNavigateToPacking = {
                    navController.navigate(
                        Screen.PackingList.createRoute(tripId)
                    )
                },

                onNavigateToNotes = {
                    navController.navigate(
                        Screen.Notes.createRoute(tripId)
                    )
                },

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.ItineraryBuilder.route,
            arguments = listOf(
                navArgument("tripId") {
                    type = NavType.IntType
                }
            )
        ) { backStack ->

            ItineraryBuilderScreen(
                tripId = backStack.arguments?.getInt("tripId") ?: 0,

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Budget.route,
            arguments = listOf(
                navArgument("tripId") {
                    type = NavType.IntType
                }
            )
        ) { backStack ->

            BudgetScreen(
                tripId = backStack.arguments?.getInt("tripId") ?: 0,

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.PackingList.route,
            arguments = listOf(
                navArgument("tripId") {
                    type = NavType.IntType
                }
            )
        ) { backStack ->

            PackingListScreen(
                tripId = backStack.arguments?.getInt("tripId") ?: 0,

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Notes.route,
            arguments = listOf(
                navArgument("tripId") {
                    type = NavType.IntType
                }
            )
        ) { backStack ->

            NotesScreen(
                tripId = backStack.arguments?.getInt("tripId") ?: 0,

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Profile.route) {

            ProfileScreen(
                onLogout = {

                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.PublicTrips.route) {

            PublicTripsScreen(
                onNavigateToTrip = { id: Int ->

                    navController.navigate(
                        Screen.PublicTripDetail.createRoute(id)
                    )
                },

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.PublicTripDetail.route,
            arguments = listOf(
                navArgument("tripId") {
                    type = NavType.IntType
                }
            )
        ) { backStack ->

            PublicTripDetailScreen(
                tripId = backStack.arguments?.getInt("tripId") ?: 0,

                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Profile")
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),

            verticalArrangement = Arrangement.Center,

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Profile Screen")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onLogout) {
                Text("Logout")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicTripsScreen(
    onNavigateToTrip: (Int) -> Unit,
    onBack: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Public Trips")
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),

            verticalArrangement = Arrangement.Center,

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Public Trips")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onNavigateToTrip(1)
                }
            ) {
                Text("Open Sample Trip")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicTripDetailScreen(
    tripId: Int,
    onBack: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Trip #$tripId")
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),

            contentAlignment = Alignment.Center
        ) {

            Text("Public Trip Detail")
        }
    }
}