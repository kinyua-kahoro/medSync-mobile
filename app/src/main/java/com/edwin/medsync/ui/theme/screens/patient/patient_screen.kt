package com.edwin.medsync.ui.theme.screens.patient

import android.R.attr.content
import android.R.id.content
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.edwin.medsync.R
import com.edwin.medsync.model.Appointment
import com.edwin.medsync.navigation.ROUTE_DASHBOARD
import com.edwin.medsync.navigation.ROUTE_HOME
import com.edwin.medsync.navigation.ROUTE_PATIENT
import com.edwin.medsync.navigation.ROUTE_PATIENT_PROFILE
import com.edwin.medsync.navigation.ROUTE_PROFILE
import com.edwin.medsync.ui.theme.screens.dashboard.Dashboard_Screen
import com.edwin.medsync.ui.theme.screens.dashboard.SampleContent
import com.edwin.medsync.utils.FirebaseService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlin.collections.isNotEmpty
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ExitToApp
import com.edwin.medsync.navigation.ROUTE_LOGIN

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Patient_Screen(navController: NavHostController,firebaseService: FirebaseService) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val patientId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    val context = LocalContext.current
    var userName by remember { mutableStateOf("") }
    var appointmentCount by remember { mutableStateOf(0) }
    var nextAppointment by remember { mutableStateOf<Appointment?>(null) }
    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            firebaseService.getPatientProfile(userId) { user ->
                userName = user.fullName
            }
        }
    }
    LaunchedEffect(patientId) {
        firebaseService.getAppointmentsForPatient(patientId) { appointments ->
            appointmentCount = appointments.size
        }
    }
    LaunchedEffect(patientId) {
        firebaseService.getAppointmentsForPatient(patientId) { appointments ->
            nextAppointment = appointments
                .filter { it.status == "approved" } // or "pending"
                .sortedBy { it.date + it.time }     // adjust sorting if needed
                .firstOrNull()
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
                    title = { Text("MedSync", color = Color.White) },
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
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Card(
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Welcome back,",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (userName.isNotEmpty()) "$userName 👋" else "Loading...",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatCard(title = "Appointments", count = appointmentCount)
                    NextAppointmentCard(appointment = nextAppointment)
                }

                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickActionCard("Book Appointment", R.drawable.ic_appointment) {
                        navController.navigate("book_appointment/$patientId")
                    }
                    QuickActionCard("View History", R.drawable.ic_history) {
                        navController.navigate("medical_history/$patientId")
                    }
                }
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                UpcomingAppointments(patientId = currentUserId, firebaseService = firebaseService)
            }
        }
    }
}


@Composable
fun StatCard(title: String, count: Int) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .shadow(6.dp, shape = RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun QuickActionCard(title: String, iconRes: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun UpcomingAppointments(patientId: String, firebaseService: FirebaseService) {
    var appointments by remember { mutableStateOf<List<Appointment>>(emptyList()) }

    LaunchedEffect(Unit) {
        firebaseService.getAppointmentsForPatient(patientId) {
            appointments = it
        }
    }

    if (appointments.isNotEmpty()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Upcoming Appointments",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            appointments.forEach { appointment ->
                Card(
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Date: ${appointment.date}")
                        Text("Time: ${appointment.time}")
                        Text("Reason: ${appointment.reason}")
                        Text("Status: ${appointment.status?.replaceFirstChar { it.uppercase() }}")
                    }
                }
            }
        }
    } else {
        Text(
            text = "No upcoming appointments",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun NextAppointmentCard(appointment: Appointment?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
        ) {
            Text(
                text = "Next Appointment",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
            Spacer(Modifier.height(12.dp))

            if (appointment != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = appointment.date ?:"N/A",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = appointment.time ?: "N/A",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = appointment.reason ?: "N/A",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                Text(
                    text = "You don't have any upcoming appointments.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}





