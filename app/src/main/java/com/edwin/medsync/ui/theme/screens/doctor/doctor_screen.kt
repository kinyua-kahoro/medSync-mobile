package com.edwin.medsync.ui.theme.screens.doctor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.edwin.medsync.R
import com.edwin.medsync.model.Appointment
import com.edwin.medsync.navigation.*
import com.edwin.medsync.utils.FirebaseService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Doctor_Screen(navController: NavHostController, firebaseService: FirebaseService) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var userName by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            firebaseService.getDoctorProfile(userId) { user ->
                userName = user.fullName
            }
        }
    }
    val appointments = remember { mutableStateListOf<Appointment>() }

    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            firebaseService.getAppointmentsForDoctor(userId) { result ->
                appointments.clear()
                appointments.addAll(result)
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
    ) {
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
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))


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

                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = "Upcoming Appointments",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                )


                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    appointments.forEach { appointment ->
                        AppointmentItem(
                            patientName = appointment.fullName ?: "Unknown",
                            time = appointment.time ?: "N/A",
                            reason = appointment.reason ?: "No reason provided"
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun AppointmentItem(patientName: String, time: String, reason: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = patientName, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Time: $time", style = MaterialTheme.typography.bodySmall)
            Text(text = "Reason: $reason", style = MaterialTheme.typography.bodySmall)
        }
    }
}

