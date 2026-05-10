package com.example.traveloop

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    vm: AuthViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()

    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailErr by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is AuthUiState.Success) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Slate900, Slate800, Slate900)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(Amber400, Teal600))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Flight,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Traveloop",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = White
            )
            Text(
                text = "Plan. Explore. Loop.",
                style = MaterialTheme.typography.bodyMedium,
                color = Slate200.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(40.dp))

            if (state is AuthUiState.Error) {
                ErrorMessage((state as AuthUiState.Error).message)
                Spacer(Modifier.height(8.dp))
            }

            TraveloopTextField(
                value = email,
                onValueChange = { email = it; emailErr = "" },
                label = "Email",
                leadingIcon = Icons.Default.Email,
                isError = emailErr.isNotBlank(),
                errorText = emailErr
            )

            Spacer(Modifier.height(12.dp))

            TraveloopTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                leadingIcon = Icons.Default.Lock,
                isPassword = true
            )

            Spacer(Modifier.height(24.dp))

            TraveloopButton(
                text = if (state is AuthUiState.Loading) "Signing in…" else "Sign In",
                onClick = {
                    if (email.isBlank()) emailErr = "Email is required"
                    else vm.login(email.trim(), password)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = state !is AuthUiState.Loading
            )

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Don't have an account? ",
                    color = Slate200.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(onClick = { vm.reset(); onNavigateToRegister() }) {
                    Text("Sign Up", color = Amber400, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    vm: AuthViewModel = viewModel()
) {
    val state by vm.uiState.collectAsState()

    var name       by remember { mutableStateOf("") }
    var email      by remember { mutableStateOf("") }
    var password   by remember { mutableStateOf("") }
    var confirm    by remember { mutableStateOf("") }
    var confirmErr by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is AuthUiState.Success) onRegisterSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Slate900, Slate800, Slate900)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(Teal600, Amber400))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = White, modifier = Modifier.size(36.dp))
            }

            Spacer(Modifier.height(20.dp))

            Text("Create Account", fontSize = 28.sp, fontWeight = FontWeight.Black, color = White)
            Text("Join the loop", style = MaterialTheme.typography.bodyMedium, color = Slate200.copy(alpha = 0.7f))

            Spacer(Modifier.height(32.dp))

            if (state is AuthUiState.Error) {
                ErrorMessage((state as AuthUiState.Error).message)
                Spacer(Modifier.height(8.dp))
            }

            TraveloopTextField(value = name, onValueChange = { name = it }, label = "Full Name", leadingIcon = Icons.Default.Person)
            Spacer(Modifier.height(12.dp))
            TraveloopTextField(value = email, onValueChange = { email = it }, label = "Email", leadingIcon = Icons.Default.Email)
            Spacer(Modifier.height(12.dp))
            TraveloopTextField(value = password, onValueChange = { password = it }, label = "Password", leadingIcon = Icons.Default.Lock, isPassword = true)
            Spacer(Modifier.height(12.dp))
            TraveloopTextField(
                value = confirm,
                onValueChange = { confirm = it; confirmErr = "" },
                label = "Confirm Password",
                leadingIcon = Icons.Default.Lock,
                isPassword = true,
                isError = confirmErr.isNotBlank(),
                errorText = confirmErr
            )

            Spacer(Modifier.height(24.dp))

            TraveloopButton(
                text = if (state is AuthUiState.Loading) "Creating account…" else "Create Account",
                onClick = {
                    if (password != confirm) {
                        confirmErr = "Passwords do not match"
                    } else {
                        vm.register(name.trim(), email.trim(), password)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = state !is AuthUiState.Loading
            )

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account? ", color = Slate200.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = { vm.reset(); onNavigateToLogin() }) {
                    Text("Sign In", color = Amber400, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}