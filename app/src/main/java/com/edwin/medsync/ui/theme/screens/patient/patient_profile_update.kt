package com.edwin.medsync.ui.theme.screens.patient

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
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.edwin.medsync.R
import com.edwin.medsync.data.PatientViewModel
import com.edwin.medsync.navigation.ROUTE_LOGIN
import com.edwin.medsync.navigation.ROUTE_PATIENT
import com.edwin.medsync.navigation.ROUTE_PATIENT_PROFILE
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientProfileUpdate(navController: NavHostController, firebaseService: FirebaseService, viewModel: PatientViewModel = viewModel()) {
    var fullName by remember { mutableStateOf(TextFieldValue("")) }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("") }
    var availability by remember { mutableStateOf("") }
    var context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    val patientId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    // Simulate loading user data from Firebase (for example purposes)
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            firebaseService.getPatientProfile(currentUser.uid) { patient ->
                fullName = TextFieldValue(patient.fullName)
                age = patient.age.toString()
                gender = patient.gender
                specialty = patient.specialty
                availability = patient.availability.joinToString(", ")
            }
        }
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(24.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.favicon),
                            contentDescription = "App Icon",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "MedSync",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Divider()

                    Spacer(Modifier.height(12.dp))

                    listOf(
                        Triple("Home", Icons.Default.Home, ROUTE_PATIENT),
                        Triple("Medical History", Icons.Outlined.Star, "medical_history/$patientId"),
                        Triple("My Profile", Icons.Outlined.AccountCircle, ROUTE_PATIENT_PROFILE),
//                        Triple("Medication", Icons.Outlined.FavoriteBorder, "medication")
                    ).forEach { (label, icon, route) ->
                        NavigationDrawerItem(
                            label = { Text(label) },
                            selected = false,
                            icon = { Icon(icon, contentDescription = label) },
                            onClick = {
                                scope.launch { drawerState.close() }
                                if (navController.currentDestination?.route != route) {
                                    navController.navigate(route) {
                                        launchSingleTop = true
                                        restoreState = true
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    }
                                }
                            }
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

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

                    Spacer(Modifier.height(24.dp))
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Update Profile") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open()
                                else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { padding ->
            // UI for the patient profile update screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Text(
                    "Update Profile",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Full Name Input
                TextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Age Input
                TextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Gender Input
                TextField(
                    value = gender,
                    onValueChange = { gender = it },
                    label = { Text("Gender") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Save Button
                Button(
                    onClick = {
                        // Convert availability to a list
                        val availabilityList = availability.split(", ").map { it.trim() }
                        val updatedPatient = User(
                            email = currentUser?.email ?: "",  // Use email from Firebase Authentication
                            password = "",  // Handle password separately
                            userid = currentUser?.uid ?: "",  // Use UID from Firebase Authentication
                            fullName = fullName.text,
                            age = age.toInt(),
                            gender = gender,
                            role = "Patient",
                            specialty = specialty,
                            availability = availabilityList
                        )

                        // Call Firebase service to save the updated profile
                        firebaseService.UpdatePatientProfile(updatedPatient) { success ->
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text("Save Changes", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}
