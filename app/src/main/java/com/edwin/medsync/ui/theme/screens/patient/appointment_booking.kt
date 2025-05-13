package com.edwin.medsync.ui.theme.screens.patient

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.edwin.medsync.R
import com.edwin.medsync.data.AppointmentViewModel
import com.edwin.medsync.model.Appointment
import com.edwin.medsync.navigation.ROUTE_LOGIN
import com.edwin.medsync.navigation.ROUTE_PATIENT
import com.edwin.medsync.navigation.ROUTE_PATIENT_PROFILE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentFormScreen(
    doctorId: String,
    patientId: String,
    navController: NavController
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    val appointmentViewModel: AppointmentViewModel = viewModel()
    val context = LocalContext.current
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
                    title = { Text("Book Appointment") },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date") },
                    placeholder = { Text("e.g. 2025-05-15") },
                    leadingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = "Date")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time") },
                    placeholder = { Text("e.g. 10:30 AM") },
                    leadingIcon = {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Time")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason for Visit") },
                    placeholder = { Text("e.g. Headache, Consultation...") },
                    leadingIcon = {
                        Icon(Icons.Default.Info, contentDescription = "Reason")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val appointment = Appointment(
                            patientId = patientId,
                            doctorId = doctorId,
                            date = date,
                            time = time,
                            reason = reason,
                            fullName = "", // Optional: populate if needed
                            status = "pending"
                        )

                        appointmentViewModel.bookAppointment(
                            appointment = appointment,
                            onSuccess = {
                                Toast.makeText(context, "Appointment booked!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            onFailure = { errorMessage ->
                                Toast.makeText(context, "Failed: $errorMessage", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Submit Appointment", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

