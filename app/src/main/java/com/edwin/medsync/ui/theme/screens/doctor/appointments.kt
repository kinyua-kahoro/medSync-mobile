package com.edwin.medsync.ui.theme.screens.doctor

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.edwin.medsync.data.AppointmentViewModel
import com.edwin.medsync.model.Appointment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.edwin.medsync.R
import com.edwin.medsync.navigation.ROUTE_APPOINTMENTS
import com.edwin.medsync.navigation.ROUTE_DOCTOR
import com.edwin.medsync.navigation.ROUTE_DOCTOR_PROFILE
import com.edwin.medsync.navigation.ROUTE_LOGIN
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentManagementScreen(
    navController: NavController,
    viewModel: AppointmentViewModel = viewModel()
) {
    val appointmentsState = viewModel.appointments.collectAsStateWithLifecycle()
    val appointments = appointmentsState.value
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Fetch appointments when the screen is loaded
    LaunchedEffect(Unit) {
        viewModel.fetchAppointmentsForCurrentDoctor()
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
                            text = "MedSync",
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
                    .background(Color.White)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Show a loading indicator if appointments are being fetched
                if (appointments.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(8.dp))
                            Text("No appointments yet", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                else {
                    Log.d("Appointments", "Fetched appointments: $appointments")
                    LazyColumn(modifier = Modifier.padding(16.dp)) {
                        items(items = appointments) { appointment ->
                            AppointmentCard(
                                appointment = appointment,
                                navController = navController,
                                onApprove = {
                                    viewModel.updateAppointmentStatus(
                                        appointmentId = appointment.appointmentId ?: "",
                                        status = "approved"
                                    )
                                },
                                onReject = {
                                    viewModel.updateAppointmentStatus(
                                        appointmentId = appointment.appointmentId ?: "",
                                        status = "rejected"
                                    )
                                }
                            )
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: Appointment,
    navController: NavController,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    var patientName by remember { mutableStateOf("Loading...") }

    // Safe call to get patient name if patientId is not null
    LaunchedEffect(appointment.patientId) {
        appointment.patientId?.let { patientId ->
            getPatientFullName(patientId) { name ->
                patientName = name
            }
        } ?: run {
            patientName = "Unknown Patient"  // Fallback if patientId is null
        }
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                navController.navigate("patientProfileScreen/${appointment.patientId}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Top Row: Patient Name and Status Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = patientName ?: "Unknown Patient",  // Safe handling of nullable patientName
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                )

                Surface(
                    color = when (appointment.status) {
                        "approved" -> Color(0xFFD0F0C0)
                        "rejected" -> Color(0xFFFFD6D6)
                        else -> Color(0xFFE0E0E0)
                    },
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 2.dp
                ) {
                    Text(
                        text = appointment.status?.replaceFirstChar { it.uppercase() } ?: "Unknown",
                        color = when (appointment.status) {
                            "approved" -> Color(0xFF2E7D32)
                            "rejected" -> Color(0xFFC62828)
                            else -> Color(0xFF616161)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Appointment Info
            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text("Name: ${patientName}", style = MaterialTheme.typography.bodyMedium)
                Text(" Date: ${appointment.date}", style = MaterialTheme.typography.bodyMedium)
                Text(" Time: ${appointment.time}", style = MaterialTheme.typography.bodyMedium)
                Text(" Reason: ${appointment.reason}", style = MaterialTheme.typography.bodyMedium)
            }

            // Action Buttons
            if (appointment.status == "pending") {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Approve", color = Color.White)
                    }
                    Button(
                        onClick = onReject,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reject", color = Color.White)
                    }
                }
            }
        }
    }
}

fun getPatientFullName(patientId: String, onResult: (String) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val patientRef = database.getReference("patients").child(patientId)

    patientRef.get().addOnSuccessListener { snapshot ->
        val fullName = snapshot.child("fullName").getValue(String::class.java)
        onResult(fullName ?: "Unknown Patient") // Provide a default value if name is not found
    }.addOnFailureListener {
        // Handle failure
        onResult("Error fetching name")
    }
}




