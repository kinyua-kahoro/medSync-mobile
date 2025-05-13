package com.edwin.medsync.ui.theme.screens.doctor

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.edwin.medsync.model.User
import com.edwin.medsync.utils.FirebaseService
import com.edwin.medsync.ui.theme.MedSyncTheme
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.edwin.medsync.R
import com.edwin.medsync.data.DoctorViewModel
import com.edwin.medsync.navigation.ROUTE_APPOINTMENTS
import com.edwin.medsync.navigation.ROUTE_DOCTOR
import com.edwin.medsync.navigation.ROUTE_DOCTOR_PROFILE
import com.edwin.medsync.navigation.ROUTE_LOGIN
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorProfileUpdate(navController: NavHostController, firebaseService: FirebaseService, viewModel: DoctorViewModel = viewModel()) {
    var fullName by remember { mutableStateOf(TextFieldValue("")) }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("") }
    var availability by remember { mutableStateOf("") }
    var context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Simulate loading user data from Firebase (for example purposes)
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            firebaseService.getDoctorProfile(currentUser.uid) { patient ->
                fullName = TextFieldValue(patient.fullName)
                age = patient.age.toString()
                gender = patient.gender
                specialty = patient.specialty
                availability = patient.availability.joinToString(", ")
            }
        }
    }
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(24.dp))

                    // Logo and App Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.favicon),
                            contentDescription = "App Icon",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "MedSync",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider()
                    Spacer(Modifier.height(12.dp))

                    val navItems = listOf(
                        Triple("Dashboard", Icons.Default.Home, ROUTE_DOCTOR),
                        Triple("Patients", Icons.Default.AccountCircle, ROUTE_APPOINTMENTS),
                        Triple("My Profile", Icons.Default.AccountCircle, ROUTE_DOCTOR_PROFILE)
                    )

                    navItems.forEach { (label, icon, route) ->
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            selected = false,
                            icon = {
                                Icon(
                                    icon,
                                    contentDescription = label,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            onClick = {
                                scope.launch { drawerState.close() }
                                if (navController.currentDestination?.route != route) {
                                    navController.navigate(route) {
                                        launchSingleTop = true
                                        restoreState = true
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = { Text("Logout", style = MaterialTheme.typography.bodyLarge) },
                        selected = false,
                        icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Logout") },
                        onClick = {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate(ROUTE_LOGIN) {
                                popUpTo(0) // Clear backstack
                            }
                        },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        },
        drawerState = drawerState
    ){
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Update Profile",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Future Profile Settings */ }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.shadow(4.dp)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // UI for the doctor profile update screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text("Update Profile", style = MaterialTheme.typography.titleLarge)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Full Name Input
                    TextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Age Input
                    TextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("Age") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Gender Input
                    TextField(
                        value = gender,
                        onValueChange = { gender = it },
                        label = { Text("Gender") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Specialty Input
                    TextField(
                        value = specialty,
                        onValueChange = { specialty = it },
                        label = { Text("Specialty") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Availability Input
                    TextField(
                        value = availability,
                        onValueChange = { availability = it },
                        label = { Text("Availability (comma-separated)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Save Button
                    Button(
                        onClick = {
                            // Convert availability to a list
                            val availabilityList = availability.split(", ").map { it.trim() }
                            val updatedDoctor = User(
                                email = currentUser?.email ?: "",  // Use email from Firebase Authentication
                                password = "",  // Handle password separately
                                userid = currentUser?.uid ?: "",  // Use UID from Firebase Authentication
                                fullName = fullName.text,
                                age = age.toInt(),
                                gender = gender,
                                role = "Doctor",
                                specialty = specialty,
                                availability = availabilityList
                            )

                            // Call Firebase service to save the updated profile
                            firebaseService.UpdateDoctorProfile(updatedDoctor) { success ->
                                if (success) {
                                    Toast.makeText(
                                        context,
                                        "Profile updated successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Failed to update profile.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Changes")
                    }
                }
            }
        }
    }
}

